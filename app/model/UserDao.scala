package model

import com.google.inject.Inject
import model.Model.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

class UserDao @Inject() (val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  def all = db.run(Users.result)
  def create(user: User) = db.run(Users += user)
  def delete(id: Long) = db.run(Users.filter(_.id === id).delete)
  def update(id: Long, user: User) = db.run(Users.filter(_.id === id).update(user))

  class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def lastname = column[String]("name")
    def login = column[String]("login")
    def passwordHash = column[String]("password_hash")

    def * = (id.?, name.?, lastname.?, login, passwordHash) <> (User.tupled, User.unapply)
  }

  val Users = TableQuery[UserTable]

}
