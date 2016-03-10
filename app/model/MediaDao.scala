package model

import javax.inject.Inject

import model.Model.Media
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future


class MediaDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val languageDao: LanguageDao) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  import languageDao.Languages

  def all(): Future[Seq[Media]] = db.run(Medias.sortBy(_.name).result)
  def byId(id: Long): Future[Option[Media]] = db.run(Medias.filter(_.id === id).result.headOption)
  def insert(media: Media) = db.run(Medias += media)
  def delete(id: Long) = db.run(Medias.filter(_.id === id).delete)
  def update(id: Long, media: Media) = db.run(Medias.filter(_.id === id).update(media))

  class MediaTable(tag: Tag) extends Table[Media](tag, "media") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def mediaUrl = column[String]("media_url")
    def languageId = column[Int]("language_id")

    def language = foreignKey("language_id", languageId, Languages)(_.id)

    def * = (id.?, name, mediaUrl, languageId) <> (Media.tupled, Media.unapply)
  }

  val Medias = TableQuery[MediaTable]

}
