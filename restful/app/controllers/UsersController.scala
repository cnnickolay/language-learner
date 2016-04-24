package controllers

import com.google.inject.Inject
import model._
import play.api.libs.json._
import play.api.mvc.{Controller, Result}
import utils.actions.{CORSAction, ActionsConfiguration, AuthTokenRefreshAction, UserAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class UsersController @Inject()(val userDao: UserDao,
                                val userAction: UserAction,
                                val authTokenRefreshAction: AuthTokenRefreshAction,
                                val corsAction: CORSAction) extends Controller with ActionsConfiguration {

  case class UserJson(id: Option[Long] = None, login: String, passwordHash: Option[String] = None, name: Option[String], lastname: Option[String], role: Option[String])

  implicit val userFormats = Json.format[UserJson]

  def users = authAction.async { request =>
    val result = for {
      user <- request.user
      userId <- user.id
    } yield user.roleId match {
      case GodRoleEnum.id =>
        userDao.all.map { users =>
          Ok(Json.toJson(users: Seq[UserJson]))
        }
      case TeacherRoleEnum.id =>
        userDao.allUsersByOwner(userId).map { users =>
          Ok(Json.toJson(users: Seq[UserJson]))
        }
      case _ => Future(InternalServerError("Role can't be identified"))
    }

    result.getOrElse(Future(BadRequest("Not logged in")))
  }

  def create = authAction.async { request =>
    val result: Result = request.body.asJson.map { json =>
      json.validate[UserJson].map { userJson =>
        userJsonToUser(userJson) match {
          case Some(user) =>
            userDao.create(user.copy(ownerUserId = request.user.flatMap(_.id)))
            Ok(s"User ${user.login} was successfully added")
          case None => BadRequest("Some data is missing for user creation")
        }
      }.getOrElse(BadRequest("Bad json format"))
    }.getOrElse(BadRequest("Json can't be parsed"))

    Future(result)
  }

  def userJsonToUser(user: UserJson): Option[User] = {
    for {
      passwordHash <- user.passwordHash
      roleName <- user.role
      role <- roleByName(roleName)
    } yield User(login = user.login, statusId = ActiveEnum.id, name = user.name, lastname = user.lastname, passwordHash = passwordHash, roleId = role.id)
  }

  implicit def userToUserJson(user: User): UserJson = {
    UserJson(id = user.id, login = user.login, name = user.name, lastname = user.lastname, role = roleById(user.roleId).map(_.name))
  }

  implicit def usersToUsersJson(users: Seq[User]): Seq[UserJson] = {
    users.map(userToUserJson)
  }
}
