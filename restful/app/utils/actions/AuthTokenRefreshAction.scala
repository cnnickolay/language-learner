package utils.actions

import com.google.inject.Inject
import model.AuthTokenDao
import play.api.Logger
import play.api.mvc.{ActionFunction, Result}
import utils.{TimeConversion, TimeService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthTokenRefreshAction @Inject()(val authTokenDao: AuthTokenDao, val timeService: TimeService) extends ActionFunction[UserRequest, UserRequest] with TimeConversion {

  val l = Logger(classOf[AuthTokenRefreshAction])

  def invokeBlock[A](request: UserRequest[A], block: (UserRequest[A]) => Future[Result]): Future[Result] = {
    val refreshToken: Option[Future[Int]] = for {
      authToken <- request.authToken
      user <- request.user
    } yield {
      l.debug(s"Refreshing expiration date of token ${authToken.token}")
      authTokenDao.refreshToken(authToken.token, timeService.now.plusMinutes(user.sessionDuration))
    }

    val refreshTokenFuture = refreshToken match {
      case Some(future: Future[_]) => future
      case _ => Future(None)
    }

    refreshTokenFuture.flatMap { _ =>
      block(request)
    }
  }

}
