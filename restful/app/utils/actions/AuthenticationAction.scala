package utils.actions

import play.api.Logger
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object AuthenticationAction extends ActionFilter[UserRequest] {

  val l = Logger(AuthenticationAction.getClass)

  def filter[A](request: UserRequest[A]): Future[Option[Result]] = Future {
    request.user match {
      case Some(user) => None
      case None => l.debug("Authentication failed"); Some(Results.Unauthorized)
    }
  }

}
