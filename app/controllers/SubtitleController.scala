package controllers

import com.google.inject.Inject
import model.SubtitleDao
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext


class SubtitleController @Inject() (subtitleDao: SubtitleDao) extends Controller {

  def getAll(id: Long) = Action.async {
    for {
      subtitles <- subtitleDao.all(id)
    } yield Ok(Json.toJson(subtitles))
  }

}
