package utils.actions

import java.sql.Timestamp

import com.google.inject.Inject
import model.AuthTokenDao
import org.joda.time.DateTime
import play.api.Logger
import play.api.mvc.{ActionFunction, Result}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class AuthTokenRefreshAction @Inject()(val authTokenDao: AuthTokenDao) extends ActionFunction[UserRequest, UserRequest] {

  val l = Logger("utils.actions")

  def invokeBlock[A](request: UserRequest[A], block: (UserRequest[A]) => Future[Result]): Future[Result] = {
    for {
      result <- block(request)
    } yield {
      request.authToken match {
        case None =>
        case Some(authToken) =>
          request.user match {
            case None =>
            case Some(user) =>
              l.debug(s"Refreshing expiration date of token ${authToken.token}")
              authTokenDao.refreshToken(authToken.token, new Timestamp(new DateTime().plusMinutes(user.sessionDuration).getMillis))
          }
      }
      result
    }
  }
}
