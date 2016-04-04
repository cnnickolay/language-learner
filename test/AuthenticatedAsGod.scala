import model.{GodRoleEnum, ActiveEnum, User, UserDao}
import slick.backend.DatabaseConfig
import slick.dbio
import slick.driver.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration.Duration._
import scala.concurrent.ExecutionContext.Implicits.global

trait AuthenticatedAsGod {
  val dbProvider: DatabaseConfig[JdbcProfile]
  val userDao: UserDao
  val godRoleUser = User(Some(2), Some("a"), Some("b"), "niko", "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", ActiveEnum.id, 30, GodRoleEnum.id)

  import dbProvider.driver.api._

  Await.result(dbProvider.db.run(
    dbio.DBIO.seq(
      userDao.Users += godRoleUser
    )
  ), Inf)

}
