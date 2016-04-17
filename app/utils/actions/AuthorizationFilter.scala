package utils.actions

import model.{RoleEnum, AdminRoleEnum}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthorizationFilter(role: RoleEnum) extends ActionFilter[UserRequest] {

  def filter[A](request: UserRequest[A]): Future[Option[Result]] = Future {
    request.user match {
      case Some(user) if user.roleId == role.id => None
      case _ => Some(Results.Forbidden)
    }
  }

}
