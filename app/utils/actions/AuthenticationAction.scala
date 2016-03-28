package utils.actions

import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object AuthenticationAction extends ActionFilter[UserRequest] {

  def filter[A](request: UserRequest[A]): Future[Option[Result]] = Future {
    request user match {
      case Some(user) => None
      case None => Some(Results.Forbidden)
    }
  }

}
