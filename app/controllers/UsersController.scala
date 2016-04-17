package controllers

import com.google.inject.Inject
import model._
import play.api.libs.json._
import play.api.mvc.{Controller, Result}
import utils.actions.{ActionsConfiguration, AuthTokenRefreshAction, UserAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class UsersController @Inject()(val userDao: UserDao,
                                val userAction: UserAction,
                                val authTokenRefreshAction: AuthTokenRefreshAction) extends Controller with ActionsConfiguration {

  case class UserJson(id: Long, login: String)

  implicit val userFormats = Json.format[UserJson]

  def users = authAction.async { request =>
    val result: Option[Future[Result]] = request.user.flatMap(_.id) zip request.user.map(_.roleId) map { case (userId, role) =>
      role match {
        case GodRoleEnum.id =>
          userDao.all.map { users =>
            Ok(Json.toJson(usersToUsersJson(users)))
          }
        case TeacherRoleEnum.id =>
          userDao.allUsersByOwner(userId).map { users =>
            Ok(Json.toJson(usersToUsersJson(users)))
          }
        case _ => Future(InternalServerError("Role can't be identified"))
      }
    } headOption

    result.getOrElse(Future(BadRequest))
  }

  def usersToUsersJson(users: Seq[User]): Seq[UserJson] = {
    users.map { user =>
      UserJson(user.id.getOrElse(0), user.login)
    }
  }
}
