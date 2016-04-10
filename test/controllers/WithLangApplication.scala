package controllers

import model._
import org.specs2.execute.{AsResult, Result}
import play.api.Application
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.WithApplication
import slick.driver.JdbcProfile

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

  override def around[T](t: => T)(implicit evidence$2: AsResult[T]): Result = {
    val databaseApi = app.injector.instanceOf[DBApi]
    Evolutions.cleanupEvolutions(databaseApi.database("default"))
    Evolutions.applyEvolutions(databaseApi.database("default"))

    super.around(t)
  }

}
