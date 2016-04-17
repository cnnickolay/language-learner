package model

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

sealed abstract class LanguageEnum(val id: Int, val name: String)
case object EnglishLanguageEnum extends LanguageEnum(1, "english")
case object FrenchLanguageEnum extends LanguageEnum(2, "french")
case object GermanLanguageEnum extends LanguageEnum(3, "german")

case class Language(id: Option[Int], name: String)

class LanguageDao @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  def all(): Future[Seq[Language]] = db.run(Languages.result)
  def byId(id: Int) = db.run(Languages.filter(_.id === id).result.headOption)

  class LanguageTable(tag: Tag) extends Table[Language](tag, "language") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id.?, name) <> (Language.tupled, Language.unapply)
  }

  val Languages = TableQuery[LanguageTable]
}
