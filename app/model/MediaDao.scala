package model

import model.Model.Media

import scala.concurrent.Future

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile


import scala.concurrent.Future


class MediaDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  def all(): Future[Seq[Media]] = db.run(Medias.result)
  def insert(media: Media) = db.run(Medias += media)
  def delete(id: Long) = db.run(Medias.filter(_.id === id).delete)

  private class MediaTable(tag: Tag) extends Table[Media](tag, "media") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (id.?, name) <> (Media.tupled, Media.unapply)
  }

  private val Medias = TableQuery[MediaTable]

}
