package utils.actions

import model.{RoleEnum, AdminRoleEnum}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthorizationFilter(roles: RoleEnum*) extends ActionFilter[UserRequest] {

  def filter[A](request: UserRequest[A]): Future[Option[Result]] = Future {
    request.user match {
      case Some(user) if roles.map(_.id).contains(user.roleId) => None
      case _ => Some(Results.Forbidden)
    }
  }

}

object AuthorizationFilter {
  def apply(roles: RoleEnum*): AuthorizationFilter = new AuthorizationFilter(roles: _*)
}