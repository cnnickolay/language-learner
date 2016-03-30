package controllers

import java.sql.Timestamp
import java.util.UUID

import com.google.inject.Inject
import model.{AuthToken, AuthTokenDao, User, UserDao}
import org.joda.time.DateTime
import play.api.Logger
import play.api.mvc.Controller
import utils.HashUtils
import utils.actions.{UserAction, CORSAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class AuthenticationController @Inject()(val userDao: UserDao,
                                         val authTokenDao: AuthTokenDao,
                                         val userAction: UserAction) extends Controller {

  val l = Logger("utils.actions")

  def obtainToken = CORSAction.async { request =>
    val tokenMonad = for {
      login <- request.headers.get("login")
      password <- request.headers.get("password")
    } yield {
      val passwordHash = HashUtils.calculateSha256(password)
      authTokenDao.findActiveTokenByUser(login, passwordHash) flatMap {
        case foundToken@Some(token) => l.debug(s"Existing token found ${token.token}"); Future(foundToken)
        case None => l.debug("Token not found, creating a new one"); userDao.byLoginAndPassword(login, passwordHash)
      } map {
        case None => l.debug(s"User with login $login and password hash $passwordHash not found"); None
        case Some(user: User) =>
          l.debug(s"User $login found. Creating new security token")
          val now = new DateTime()
          val userId: Long = user.id.getOrElse(-1)
          val token = AuthToken(
            UUID.randomUUID().toString,
            new Timestamp(now.getMillis),
            new Timestamp(new DateTime(now.getMillis).plusMinutes(user.sessionDuration).getMillis),
            None, active = true, userId)
          Await.ready(authTokenDao.create(token), Duration.Inf)
          Some(token)
        case Some(token: AuthToken) => Some(token)
        case x @ _ => l.error(s"Not defined $x"); None
      }
    } map {
      case None => Forbidden
      case Some(token) => Ok(token.token)
    }

    tokenMonad match {
      case None => l.debug("login or passwordHash headers are not defined"); Future(Forbidden)
      case Some(monad) => monad
    }
  }

  def revokeToken = (CORSAction andThen userAction).async { request =>
    request.authToken match {
      case None => Future(BadRequest("No token to cancel"))
      case Some(token) => authTokenDao.revokeToken(token.token).map(_ => Ok("Token has been revoked"))
    }
  }

}
