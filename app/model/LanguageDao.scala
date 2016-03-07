package model

import com.google.inject.Inject
import model.Model.Language
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

class LanguageDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  def all() = db.run(Languages.result)
  def byId(id: Int) = db.run(Languages.filter(_.id === id).result.headOption)

  class LanguageTable(tag: Tag) extends Table[Language](tag, "language") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id.?, name) <> (Language.tupled, Language.unapply)
  }

  val Languages = TableQuery[LanguageTable]
}
