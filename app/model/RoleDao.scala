package model

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

sealed abstract class RoleEnum(val id: Int, val name: String)
case object GodRoleEnum extends RoleEnum(1, "god")
case object AdminRoleEnum extends RoleEnum(2, "admin")
case object TeacherRoleEnum extends RoleEnum(3, "teacher")
case object StudentRoleEnum extends RoleEnum(4, "student")

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
