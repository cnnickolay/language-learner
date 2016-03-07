package controllers

import com.google.inject.Inject
import model.{SubtitleDao, MediaDao}
import model.Model._
import play.api.libs.json._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

class MediaController @Inject()(mediaDao: MediaDao, subtitleDao: SubtitleDao) extends Controller {

  def getAll = Action.async {
    for {
      medias <- mediaDao.all()
    } yield Ok(Json.toJson(medias))
  }

  def byId(id: Long) = Action.async {
    for {
      byId <- mediaDao.byId(id)
    } yield Ok(Json.toJson(byId))
  }

  def create = Action.async { request =>
    request.body.asJson.map { (json: JsValue) =>
      json.validate[Media] match {
        case JsSuccess(media: Media, _) => mediaDao.insert(media); Future{Ok("inserted media item")}
        case JsError(e) => Future{ BadRequest(s"Bad payload: $e") }
      }
    }.getOrElse(Future{BadRequest("Unable to parse payload")})
  }

  def delete(id: Long) = Action.async {
    Future{
    }.flatMap { _ =>
      subtitleDao.deleteAll(id)
    }.flatMap{ _ =>
      mediaDao.delete(id)
    }.flatMap{ _ =>
      Future(Ok(s"Media with id $id deleted"))
    }
  }

  def update(id: Long) = Action.async { request =>
    request.body.asJson.map( json =>
      json.validate[Media] match {
        case JsSuccess(media, _) => mediaDao.update(id, media); Future{Ok(s"media item $id was successfully updated")}
        case JsError(e) => Future{ BadRequest(s"Bad payload: $e")}
      }
    ).getOrElse(Future{BadRequest("Unable to parse payload")})
  }

}
