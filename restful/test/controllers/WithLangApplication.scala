package controllers

import model._
import org.specs2.execute.{AsResult, Result}
import org.specs2.specification.{Before, Around}
import play.api.Application
import play.api.db.{Database, DBApi}
import play.api.db.evolutions.{Evolution, SimpleEvolutionsReader, Evolutions}
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
  lazy val roleDao = app.injector.instanceOf[RoleDao]
  lazy val statusDao = app.injector.instanceOf[StatusDao]
  lazy val courseDao = app.injector.instanceOf[CourseDao]

  def initUsers: Seq[User] = Seq()
  def initAuthTokens: Seq[AuthToken] = Seq()
  def sqlTestData: Seq[String] = Seq()

  override def around[T](t: => T)(implicit evidence$2: AsResult[T]): Result = {
    val databaseApi = app.injector.instanceOf[DBApi]
    val database: Database = databaseApi.database("default")

    database.getConnection().prepareStatement("drop schema public cascade;").execute()
    database.getConnection().prepareStatement("create schema public;").execute()

    Evolutions.applyEvolutions(database)

    sqlTestData.foreach { sql =>
      database.getConnection().prepareStatement(sql).execute()
    }

    import dbProvider.driver.api._

    val _users = initUsers
    val _authTokens = initAuthTokens
    _users.foreach(value => println(s"++$value"))
    _authTokens.foreach(value => println(s"++$value"))

    val seq: dbio.DBIOAction[Unit, NoStream, Write] = dbio.DBIO.seq(
      userDao.Users ++= _users,
      authTokenDao.AuthTokens ++= _authTokens
    )
    Await.result(dbProvider.db.run(seq), Inf)

    super.around(t)
  }


}
