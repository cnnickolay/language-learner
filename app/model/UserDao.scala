package model

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

case class User(id: Option[Long] = None,
                name: Option[String] = None,
                lastname: Option[String] = None,
                login: String,
                passwordHash: String,
                statusId: Int,
                sessionDuration: Int,
                roleId: Int)

class UserDao @Inject() (val dbConfigProvider: DatabaseConfigProvider,
                         val userStatusDao: StatusDao,
                         val roleDao: RoleDao) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  import userStatusDao.Statuses
  import roleDao.Roles

  def all: Future[Seq[User]] = db.run(Users.result)
  def byLoginAndPassword(login: String, passwordHash: String) = db.run {
    Users.filter(user => user.login === login && user.passwordHash === passwordHash).result.headOption
  }
  def create(user: User) = db.run(Users += user)
  def delete(id: Long) = db.run(Users.filter(_.id === id).delete)
  def update(id: Long, user: User) = db.run(Users.filter(_.id === id).update(user))

  class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def lastname = column[String]("lastname")
    def login = column[String]("login")
    def passwordHash = column[String]("password_hash")
    def statusId = column[Int]("status_id")
    def sessionDuration = column[Int]("session_duration")
    def roleId = column[Int]("role_id")

    def status = foreignKey("user_status_id_fkey", statusId, Statuses)(_.id)
    def role = foreignKey("user_role_id_fkey", roleId, Roles)(_.id)

    def * = (id.?, name.?, lastname.?, login, passwordHash, statusId, sessionDuration, roleId) <> (User.tupled, User.unapply)
  }

  val Users = TableQuery[UserTable]

}
