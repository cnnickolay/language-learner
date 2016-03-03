package controllers

import com.google.inject.Inject
import model.Model.Subtitle
import model.SubtitleDao
import play.api.libs.json._
import play.api.mvc.{Result, Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future


class SubtitleController @Inject() (subtitleDao: SubtitleDao) extends Controller {

  def getAll(id: Long) = Action.async {
    for {
      subtitles <- subtitleDao.all(id)
    } yield Ok(Json.toJson(subtitles))
  }

  def byId(mediaId: Long, subtitleId: Long) = Action.async {
    for {
      subtitle <- subtitleDao.byId(mediaId, subtitleId)
    } yield Ok(Json.toJson(subtitle))
  }

  def update(mediaId: Long, subtitleId: Long) = Action.async { request =>
    request.body.asJson.map( json =>
      json.validate[Subtitle] match {
        case JsSuccess(subtitle, _) =>
          for {
            oldSubtitle <- subtitleDao.byId(mediaId, subtitleId)
            displacedSub <- subtitleDao.byPos(mediaId, subtitle.pos)
          } yield {
            oldSubtitle match {
              case Some(oldSubtitleValue) =>
                if (oldSubtitleValue.pos != subtitle.pos) {
                  subtitleDao.update(mediaId, subtitleId, subtitle)
                  displacedSub match {
                    case Some(displacedSubValue) => subtitleDao.update(displacedSubValue.mediaId, displacedSubValue.id.get, displacedSubValue.copy(pos = oldSubtitleValue.pos)); Ok("")
                    case None => Ok("")
                  }
                } else {
                  subtitleDao.update(mediaId, subtitleId, subtitle); Ok("")
                }
              case None => Ok("")
            }
          }
        case JsError(e) => Future(BadRequest("Unable to parse json"))
      }
    ).getOrElse(Future(BadRequest("Unable to process request")))
  }

  def create(mediaId: Long) = Action.async { request =>
    request.body.asJson.map { json =>

      json.validate[Subtitle] match {
        case JsSuccess(subtitle, _) => subtitleDao.create(subtitle).flatMap(_ => Future{Ok("new subtitle added")})
        case JsError(e) => Future(BadRequest("Unable to parse json"))
      }

    }.getOrElse(Future(BadRequest("Unable to process request")))
  }

  def delete(mediaId: Long, subtitleId: Long) = Action.async {
    for {
      _ <- subtitleDao.delete(mediaId, subtitleId)
    } yield Ok(s"Subtitle $subtitleId of media $mediaId deleted")
  }
}
