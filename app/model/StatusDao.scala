package model

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

sealed abstract class UserStatusEnum(val id: Integer, val status: String)
case object ActiveEnum extends UserStatusEnum(1, "active")
case object CancelledEnum extends UserStatusEnum(2, "cancelled")
case object SuspendedEnum extends UserStatusEnum(3, "suspended")

case class Status(id: Int, name: String)

class StatusDao @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  def all: Future[Seq[Status]] = db.run(Statuses.result)

  class StatusTable(tag: Tag) extends Table[Status](tag, "user_status") {
    def id = column[Int]("id", O.PrimaryKey)
    def name = column[String]("name")

    def * = (id, name) <> (Status.tupled, Status.unapply)
  }

  val Statuses = TableQuery[StatusTable]

}
