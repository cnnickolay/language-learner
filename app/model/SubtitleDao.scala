package model

import com.google.inject.Inject
import model.Model.Subtitle
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

class SubtitleDao @Inject() (val dbConfigProvider: DatabaseConfigProvider, val mediaDao: MediaDao) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  import mediaDao.Medias

  def all(mediaId: Long) = db.run(Subtitles.filter(_.mediaId === mediaId).result)

  class SubtitleTable(tag: Tag) extends Table[Subtitle](tag, "subtitle") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def pos = column[Int]("pos")
    def offset = column[BigDecimal]("offset")
    def text = column[String]("text")
    def mediaId = column[Long]("media_id")
    def media = foreignKey("media_id", mediaId, Medias)(_.id)

    def * = (id.?, pos, offset, text, mediaId) <> (Subtitle.tupled, Subtitle.unapply)
  }

  var Subtitles = TableQuery[SubtitleTable]

}
