package model

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

case class Media(id: Option[Long], name: String, description: Option[String], mediaUrl: String, mediaGroupId: Option[Long])

class MediaDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val mediaGroupDao: MediaGroupDao) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  import mediaGroupDao.MediaGroups

  def all(): Future[Seq[Media]] = db.run(Medias.sortBy(_.name).result)
  def byMediaGroup(mediaGroupId: Long) = db.run(Medias.filter(_.mediaGroupId === mediaGroupId).sortBy(_.name).result)
  def byId(id: Long): Future[Option[Media]] = db.run(Medias.filter(_.id === id).result.headOption)
  def insert(media: Media) = db.run(Medias += media)
  def delete(id: Long) = db.run(Medias.filter(_.id === id).delete)
  def update(id: Long, media: Media) = db.run(Medias.filter(_.id === id).update(media))

  class MediaTable(tag: Tag) extends Table[Media](tag, "media") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def description = column[String]("description")
    def mediaUrl = column[String]("media_url")
    def mediaGroupId = column[Long]("media_group_id")

    def mediaGroup = foreignKey("media_media_group_id_fkey", mediaGroupId, MediaGroups)(_.id)

    def * = (id.?, name, description.?, mediaUrl, mediaGroupId.?) <> (Media.tupled, Media.unapply)
  }

  val Medias = TableQuery[MediaTable]
}
