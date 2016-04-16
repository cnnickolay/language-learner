package utils.actions

import com.google.inject.Inject
import model.{AuthToken, AuthTokenDao, User, UserDao}
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import utils.{TimeConversion, TimeService}

import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}

class UserRequest[A](val user: Option[User] = None, val authToken: Option[AuthToken] = None, request: Request[A]) extends WrappedRequest[A](request)

class UserAction @Inject()(val userDao: UserDao, val authTokenDao: AuthTokenDao, val timeService: TimeService) extends ActionTransformer[Request, UserRequest] with TimeConversion {

  val l = Logger(classOf[UserAction])

  def transform[A](request: Request[A]): Future[UserRequest[A]] = {
    val noUserRequest = new UserRequest(request = request)
    val token: Option[String] = request.headers.get("token")

    val userRequest: Future[UserRequest[A]] = token match {
      case None => l.debug("No token value found"); Future.successful(noUserRequest)
      case Some(tokenValue) => Future {
        l.debug(s"Token value is $tokenValue")
        Await.result(authTokenDao.findToken(tokenValue), Inf) match {
          case None => l.debug(s"No token registered with number $tokenValue"); noUserRequest
          case Some(foundToken) =>
            if (!foundToken.active) {
              l.debug(s"Token $tokenValue is expired")
              noUserRequest
            } else if (foundToken.expiresAt.before(timeService.now)) { // check if auth token is overdue
              l.debug(s"Token $tokenValue is overdue and will be cancelled")
              Await.result(authTokenDao.revokeToken(tokenValue), Inf)
              noUserRequest
            } else {
              Await.result(authTokenDao.userByToken(tokenValue), Inf) match {
                case None => l.debug(s"No user associated with token $tokenValue"); noUserRequest
                case Some(user) =>
                  l.debug(s"User ${user.login} found for token $tokenValue")
                  new UserRequest[A](user = Some(user), authToken = Some(foundToken), request = request)
              }
            }
        }
      }
    }
    userRequest
  }
}

