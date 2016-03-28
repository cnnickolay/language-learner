package model

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

case class User(id: Option[Long] = None,
                name: Option[String] = None,
                lastname: Option[String] = None,
                login: String,
                passwordHash: String,
                sessionDuration: Int)

class UserDao @Inject() (val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  def all = db.run(Users.result)
  def create(user: User) = db.run(Users += user)
  def delete(id: Long) = db.run(Users.filter(_.id === id).delete)
  def update(id: Long, user: User) = db.run(Users.filter(_.id === id).update(user))

  class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def lastname = column[String]("lastname")
    def login = column[String]("login")
    def passwordHash = column[String]("password_hash")
    def sessionDuration = column[Int]("session_duration")

    def * = (id.?, name.?, lastname.?, login, passwordHash, sessionDuration) <> (User.tupled, User.unapply)
  }

  val Users = TableQuery[UserTable]

}
