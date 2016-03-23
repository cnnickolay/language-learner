package model

import java.sql.Timestamp
import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

case class AuthToken(token: String, createdAt: Timestamp, expiresAt: Timestamp, active: Boolean = true, userId: Long)

class AuthTokenDao @Inject() (val dbConfigProvider: DatabaseConfigProvider, val userDao: UserDao) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  import userDao.Users

  def all = db.run(AuthTokens.result)
  def userByToken(token: String): Future[Option[User]] = db.run {
    val users = for {
      authToken <- AuthTokens if authToken.active === true
      user <- Users if user.id === authToken.userId
    } yield user
    users.result.headOption
  }
  def create(authToken: AuthToken) = db.run(AuthTokens += authToken)
  def expireToken(token: String) = db.run(AuthTokens.filter(_.token === token).map(_.active).update(false))

  class AuthTokenTable(tag: Tag) extends Table[AuthToken](tag, "auth_token") {
    def token = column[String]("token", O.PrimaryKey)
    def createdAt = column[Timestamp]("created_at")
    def expiresAt = column[Timestamp]("expires_at")
    def active = column[Boolean]("active")
    def userId = column[Long]("user_id")

    def user = foreignKey("user_id", userId, Users)(_.id)

    def * = (token, createdAt, expiresAt, active, userId) <> (AuthToken.tupled, AuthToken.unapply)
  }

  val AuthTokens = TableQuery[AuthTokenTable]

}
