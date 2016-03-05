package model

import com.google.inject.Inject
import model.Model.Subtitle
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

class SubtitleDao @Inject() (val dbConfigProvider: DatabaseConfigProvider, val mediaDao: MediaDao) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  import mediaDao.Medias

  def all(mediaId: Long) = db.run(Subtitles.filter(_.mediaId === mediaId).sortBy(_.offset.asc.nullsLast).result)
  def byId(mediaId: Long, subtitleId: Long) = db.run(Subtitles.filter(sub => sub.mediaId === mediaId && sub.id === subtitleId).result.headOption)

  def update(mediaId: Long, subtitleId: Long, subtitle: Subtitle) = db.run {
    Subtitles.filter(sub => sub.mediaId === mediaId && sub.id === subtitleId).update(subtitle)
  }
  def delete(mediaId: Long, subtitleId: Long) = db.run(Subtitles.filter(sub => sub.mediaId === mediaId && sub.id === subtitleId).delete)

  def create(subtitle: Subtitle) = db.run { Subtitles += subtitle }
  def totalSubtitles(mediaId: Long) = db.run(Subtitles.filter(_.mediaId === mediaId).size.result)

  class SubtitleTable(tag: Tag) extends Table[Subtitle](tag, "subtitle") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def offset = column[BigDecimal]("offset")
    def text = column[String]("text")
    def mediaId = column[Long]("media_id")
    def media = foreignKey("media_id", mediaId, Medias)(_.id)

    def * = (id.?, offset.?, text, mediaId) <> (Subtitle.tupled, Subtitle.unapply)
  }

  var Subtitles = TableQuery[SubtitleTable]

}
