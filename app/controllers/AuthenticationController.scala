package controllers

import java.sql.Timestamp
import java.util.UUID

import com.google.inject.Inject
import model.{User, AuthToken, AuthTokenDao, UserDao}
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json.{JsSuccess, Json}
import play.api.mvc.Controller
import utils.HashUtils
import utils.actions.{AuthTokenRefreshAction, ActionsConfiguration, UserAction, CORSAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import model.JsonConverters._

class AuthenticationController @Inject()(val userDao: UserDao,
                                         val authTokenDao: AuthTokenDao,
                                         val userAction: UserAction,
                                         val authTokenRefreshAction: AuthTokenRefreshAction) extends Controller with ActionsConfiguration {

  val l = Logger("utils.actions")

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
          case foundToken@Some(token) => l.debug(s"Existing token found ${token.token}"); Future(foundToken)
          case None => l.debug("Token not found, creating a new one"); userDao.byLoginAndPassword(user.login, passwordHash)
        } map {
          case None =>
            val err = s"User with login ${user.login} and password hash $passwordHash not found"
            l.debug(err)
            BadRequest(err)
          case Some(user: User) =>
            l.debug(s"User ${user.login} found. Creating new security token")
            val now = new DateTime()
            val userId: Long = user.id.getOrElse(-1)
            val token = AuthToken(
              UUID.randomUUID().toString,
              new Timestamp(now.getMillis),
              new Timestamp(new DateTime(now.getMillis).plusMinutes(user.sessionDuration).getMillis),
              None, active = true, userId)
            Await.ready(authTokenDao.create(token), Duration.Inf)
            Ok(Json.toJson(token))
          case Some(token: AuthToken) => Ok(Json.toJson(token))
          case x @ _ => l.error(s"Not defined $x"); BadRequest("Undentified error")
        }
    } getOrElse Future(BadRequest("crap"))

  }

  def revokeToken = authActionWithCORS.async { request =>
    request.authToken match {
      case None => Future(BadRequest("No token to cancel"))
      case Some(token) => authTokenDao.revokeToken(token.token).map(_ => Ok("Token has been revoked"))
    }
  }

}
