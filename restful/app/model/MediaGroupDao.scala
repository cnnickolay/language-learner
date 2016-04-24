package model

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

case class MediaGroup(id: Option[Long] = None, name: String, description: Option[String] = None, languageId: Int)

class MediaGroupDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val languageDao: LanguageDao) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  import languageDao.Languages

  def all() = db.run(MediaGroups.result)
  def byId(id: Long): Future[Option[MediaGroup]] = db.run(MediaGroups.filter(_.id === id).result.headOption)
  def byName(name: String): Future[Option[MediaGroup]] = db.run(MediaGroups.filter(_.name === name).result.headOption)
  def insert(mediaGroup: MediaGroup) = db.run(MediaGroups += mediaGroup)
  def update(id: Long, mediaGroup: MediaGroup) = db.run(MediaGroups.filter(_.id === id).update(mediaGroup))
  def delete(id: Long) = db.run(MediaGroups.filter(_.id === id).delete)

  class MediaGroupTable(tag: Tag) extends Table[MediaGroup](tag, "media_group") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def description = column[String]("description")
    def languageId = column[Int]("language_id")

    def language = foreignKey("media_group_language_id_fkey", languageId, Languages)(_.id)

    def * = (id.?, name, description.?, languageId) <> (MediaGroup.tupled, MediaGroup.unapply)
  }

  val MediaGroups = TableQuery[MediaGroupTable]

}
