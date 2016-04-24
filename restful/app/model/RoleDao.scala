package model

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

case class Role(id: Int, name: String)

class RoleDao @Inject() (val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  class RoleTable(tag: Tag) extends Table[Role](tag, "role") {
    def id = column[Int]("id", O.PrimaryKey)
    def name = column[String]("name")

    def * = (id, name) <> (Role.tupled, Role.unapply)
  }

  val Roles = TableQuery[RoleTable]
}
