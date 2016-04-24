package model

import java.sql.Timestamp
import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import utils.{TimeConversion, TimeService}

import scala.concurrent.Future

case class AuthToken(token: String,
                     createdAt: Timestamp,
                     expiresAt: Timestamp,
                     expiredAt: Option[Timestamp] = None,
                     active: Boolean = true,
                     userId: Long)

class AuthTokenDao @Inject() (val dbConfigProvider: DatabaseConfigProvider, val userDao: UserDao, val timeService: TimeService) extends HasDatabaseConfigProvider[JdbcProfile] with TimeConversion {

  import driver.api._
  import userDao.Users

  def all = db.run(AuthTokens.result)
  def userByToken(token: String): Future[Option[User]] = db.run {
    val users = for {
      authToken <- AuthTokens if authToken.token === token
      user <- Users if user.id === authToken.userId
    } yield user
    users.result.headOption
  }
  def create(authToken: AuthToken) = db.run(AuthTokens += authToken)
  def revokeToken(token: String) = db.run {
    AuthTokens.filter(_.token === token)
      .map(token => (token.active, token.expiredAt))
      .update(false, timeService.now)
  }
  def findToken(token: String): Future[Option[AuthToken]] = db.run {
    AuthTokens.filter(_.token === token).result.headOption
  }
  def findActiveToken(token: String): Future[Option[AuthToken]] = db.run {
    AuthTokens.filter(authToken => authToken.token === token && authToken.active === true).result.headOption
  }
  def refreshToken(token: String, now: Timestamp) = db.run {
    AuthTokens.filter(_.token === token).map(_.expiresAt).update(now)
  }
  def findActiveTokenByUser(login: String, passwordHash: String): Future[Option[AuthToken]] = db.run {
    val results = for {
      user <- Users if user.login === login && user.passwordHash === passwordHash
      authToken <- AuthTokens if authToken.userId === user.id && authToken.active
    } yield authToken
    results.result.headOption
  }

  class AuthTokenTable(tag: Tag) extends Table[AuthToken](tag, "auth_token") {
    def token = column[String]("token", O.PrimaryKey)
    def createdAt = column[Timestamp]("created_at")
    def expiresAt = column[Timestamp]("expires_at")
    def expiredAt = column[Timestamp]("expired_at")
    def active = column[Boolean]("active")
    def userId = column[Long]("user_id")

    def user = foreignKey("auth_token_user_id_fkey", userId, Users)(_.id)

    def * = (token, createdAt, expiresAt, expiredAt.?, active, userId) <> (AuthToken.tupled, AuthToken.unapply)
  }

  val AuthTokens = TableQuery[AuthTokenTable]

}
