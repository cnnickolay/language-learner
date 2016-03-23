package controllers

import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

object AuthAction extends ActionBuilder[Request] {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    Future {
      BadRequest("No access")
    }
//    block(request)
  }

  def apply(role: String)(block: => Future[Result]): Action[AnyContent] = {
//    async(block)
    Action {
      BadRequest("feck off")
    }
  }
}
