package controllers

import com.google.inject.Inject
import model.JsonConverters._
import model.{AuthToken, AuthTokenDao, User, UserDao}
import play.api.Logger
import play.api.libs.json.{JsSuccess, Json}
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
                                         val timeService: TimeService) extends Controller with ActionsConfiguration with TimeConversion {

  val l = Logger(classOf[AuthenticationController])

  def obtainToken = CORSAction.async { request =>
    l.debug(request.body.asText.getOrElse(""))
    request.body.asJson.map { json =>
      json.validate[UserAuth] match {
        case JsSuccess(user, _) => l.debug("User parsed");Some(user)
        case _ => l.error("Failed to parse payload"); None
      }
    } map {
      case None => l.error("Unable to parse json"); Future(BadRequest("Unable to parse json"))
      case Some(user) =>
        val passwordHash = HashUtils.calculateSha256(user.password)
        authTokenDao.findActiveTokenByUser(user.login, passwordHash) flatMap {
          case foundToken @ Some(token) =>
            val now = timeService.now
            if (now.before(token.expiresAt)) {
              for {
                Some(user) <- userDao.byLoginAndPassword(user.login, passwordHash)
                expirationDate = now.plusMinutes(user.sessionDuration)
                _ <- authTokenDao.refreshToken(token.token, expirationDate)
                authToken <- authTokenDao.findActiveToken(token.token)
              } yield {
                authToken
              }
            } else {
              authTokenDao.revokeToken(token.token).flatMap(_ => userDao.byLoginAndPassword(user.login, passwordHash))
            }
          case None =>
            l.debug("Token not found, creating a new one")
            userDao.byLoginAndPassword(user.login, passwordHash)
        } map {
          case None =>
            val err = s"User with login ${user.login} and password hash $passwordHash not found"
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
            Ok(Json.parse(s"""{"token": "$generatedToken"}"""))
          case Some(token: AuthToken) => Ok(Json.toJson(token))
          case x @ _ => l.error(s"Not defined $x"); BadRequest("Unidentified error")
        }
    } getOrElse Future(BadRequest)

  }

  def revokeToken = authActionWithCORS.async { request =>
    Future {
      request.authToken.map { authToken =>
        authTokenDao.revokeToken(authToken.token)
        Ok("Token has been revoked")
      } getOrElse BadRequest
    }
  }

}
