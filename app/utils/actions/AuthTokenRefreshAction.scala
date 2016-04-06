package utils.actions

import java.sql.Timestamp

import com.google.inject.Inject
import model.AuthTokenDao
import play.api.Logger
import play.api.mvc.{ActionFunction, Result}
import utils.{TimeConversion, TimeService}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

import scala.concurrent.{Await, Future}

class AuthTokenRefreshAction @Inject()(val authTokenDao: AuthTokenDao, val timeService: TimeService) extends ActionFunction[UserRequest, UserRequest] with TimeConversion {

  val l = Logger(classOf[AuthTokenRefreshAction])

/*
  def invokeBlock[A](request: UserRequest[A], block: (UserRequest[A]) => Future[Result]): Future[Result] = {
    val result = block(request)

    val refreshToken: Option[Future[Int]] = for {
      authToken <- request.authToken
      user <- request.user
    } yield {
      l.debug(s"Refreshing expiration date of token ${authToken.token}")
      authTokenDao.refreshToken(authToken.token, timeService.now.plusMinutes(user.sessionDuration))
    }

    for {
      _ <- refreshToken.get

    }

    //    refreshToken match {
    //      case None =>
    //      case Some(x: Future[Int]) =>
    //        x.map(_).map(_ => result)
    //    }
  }
*/

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
              val newExpirationDate = timeService.now.plusMinutes(user.sessionDuration)
              l.debug(s"Refreshing expiration date of token ${authToken.token} to ${newExpirationDate}")
              Await.result(authTokenDao.refreshToken(authToken.token, newExpirationDate), Duration.Inf)
          }
      }
      println("========== " + Await.result(authTokenDao.refreshToken("d67bc960-7891-4f2e-bfd1-9acf264524b0", timeService.now.plusMinutes(35)), Duration.Inf))
      result
    }
  }

}
