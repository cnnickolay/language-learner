package controllers

import model._
import org.specs2.execute.{AsResult, Result}
import play.api.Application
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.WithApplication
import slick.dbio
import slick.dbio.Effect.Write
import slick.dbio.{NoStream, Effect}
import slick.driver.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration.Duration._

abstract class WithLangApplication(app: Application) extends WithApplication(app) {

  lazy val databaseConfigProvider = app.injector.instanceOf[DatabaseConfigProvider]
  lazy val dbProvider = databaseConfigProvider.get[JdbcProfile]
  lazy val userDao = app.injector.instanceOf[UserDao]
  lazy val authTokenDao = app.injector.instanceOf[AuthTokenDao]
  lazy val languageDao = app.injector.instanceOf[LanguageDao]
  lazy val mediaDao = app.injector.instanceOf[MediaDao]
  lazy val mediaGroupDao = app.injector.instanceOf[MediaGroupDao]
  lazy val roleDao = app.injector.instanceOf[RoleDao]
  lazy val statusDao = app.injector.instanceOf[StatusDao]
  lazy val subtitleDao = app.injector.instanceOf[SubtitleDao]

  def initUsers: Seq[User] = Seq()
  def initAuthTokens: Seq[AuthToken] = Seq()
  def initMediaGroups: Seq[MediaGroup] = Seq()

  override def around[T](t: => T)(implicit evidence$2: AsResult[T]): Result = {
    val databaseApi = app.injector.instanceOf[DBApi]
    Evolutions.cleanupEvolutions(databaseApi.database("default"))
    Evolutions.applyEvolutions(databaseApi.database("default"))

    import dbProvider.driver.api._

    val _users = initUsers
    val _authTokens = initAuthTokens
    val _mediaGroups = initMediaGroups
    _users.foreach(value => println(s"++$value"))
    _authTokens.foreach(value => println(s"++$value"))
    _mediaGroups.foreach(value => println(s"++$value"))

    val seq: dbio.DBIOAction[Unit, NoStream, Write] = dbio.DBIO.seq(
      userDao.Users ++= _users,
      authTokenDao.AuthTokens ++= _authTokens,
      mediaGroupDao.MediaGroups ++= _mediaGroups
    )
    Await.result(dbProvider.db.run(seq), Inf)

    super.around(t)
  }

}
