package controllers

import com.google.inject.Inject
import model.{AuthToken, AuthTokenDao, User, UserDao}
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsSuccess, _}
import play.api.mvc.Controller
import utils.actions.{ActionsConfiguration, AuthTokenRefreshAction, CORSAction, UserAction}
import utils.{AuthTokenGenerator, HashUtils, TimeConversion, TimeService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class AuthenticationController @Inject()(val userDao: UserDao,
                                         val authTokenDao: AuthTokenDao,
                                         val userAction: UserAction,
                                         val authTokenRefreshAction: AuthTokenRefreshAction,
                                         val authTokenGenerator: AuthTokenGenerator,
                                         val timeService: TimeService,
                                         val corsAction: CORSAction) extends Controller with ActionsConfiguration with TimeConversion {

  val l = Logger(classOf[AuthenticationController])

  val userFormatter = (
    (__ \ "login").format[String] ~
    (__ \ "password").format[String]
  ).tupled

  val authenticationTokenFormatter = (
    (__ \ "token").format[String] ~
    (__ \ "expiresAt").format[DateTime]
  ).tupled

  def obtainToken = corsAction.async { request =>
    request.body.asJson.map { json =>
      userFormatter reads json match {
        case JsSuccess(user @ (login: String, password: String), _) => l.debug("User parsed");Some(user)
        case _ => l.error("Failed to parse payload"); None
      }
    } map {
      case None => l.error("Unable to parse json"); Future(BadRequest("Unable to parse json"))
      case Some(user @ (login: String, password: String)) =>
        val passwordHash = HashUtils.calculateSha256(password)
        authTokenDao.findActiveTokenByUser(login, passwordHash) flatMap {
          case foundToken @ Some(token) =>
            val now = timeService.now
            if (now.before(token.expiresAt)) {
              for {
                Some(user) <- userDao.byLoginAndPassword(login, passwordHash)
                expirationDate = now.plusMinutes(user.sessionDuration)
                _ <- authTokenDao.refreshToken(token.token, expirationDate)
                authToken <- authTokenDao.findActiveToken(token.token)
              } yield {
                authToken
              }
            } else {
              authTokenDao.revokeToken(token.token).flatMap(_ => userDao.byLoginAndPassword(login, passwordHash))
            }
          case None =>
            l.debug("Token not found, creating a new one")
            userDao.byLoginAndPassword(login, passwordHash)
        } map {
          case None =>
            val err = s"User with login $login and password hash $passwordHash not found"
            l.debug(err)
            Unauthorized(err)
          case Some(user: User) =>
            l.debug(s"User ${user.login} found. Creating new security token")
            val now = timeService.now
            val userId: Long = user.id.getOrElse(-1)
            val generatedToken: String = authTokenGenerator.nextAuthToken()
            val token = AuthToken(
              generatedToken,
              now,
              now.plusMinutes(user.sessionDuration),
              None, active = true, userId)
            Await.ready(authTokenDao.create(token), Duration.Inf)
            Ok(authenticationTokenFormatter writes(generatedToken, token.expiresAt))
          case Some(token: AuthToken) => Ok(authenticationTokenFormatter writes(token.token, token.expiresAt))
          case x @ _ => l.error(s"Not defined $x"); BadRequest("Unidentified error")
        }
    } getOrElse Future(BadRequest)

  }

  def revokeToken = authAction.async { request =>
    Future {
      request.authToken.map { authToken =>
        authTokenDao.revokeToken(authToken.token)
        Ok("Token has been revoked")
      } getOrElse BadRequest
    }
  }

  def refreshToken = authAction {
    Ok("Token refreshed")
  }

}
