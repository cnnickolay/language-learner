package controllers

import com.google.inject.Inject
import model.JsonConverters._
import model.{Media, MediaDao, SubtitleDao}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import utils.actions.CORSAction

import scala.concurrent.Future

class MediaController @Inject()(mediaDao: MediaDao, subtitleDao: SubtitleDao) extends Controller {

  def getAll = CORSAction.async {
    for {
      medias <- mediaDao.all()
    } yield Ok(Json.toJson(medias))
  }

  def getAllByMediaGroup(mediaGroupId: Long) = Action.async {
    for {
      medias <- mediaDao.byMediaGroup(mediaGroupId)
    } yield Ok(Json.toJson(medias))
  }

  def byId(mediaId: Long) = Action.async {
    for {
      byId <- mediaDao.byId(mediaId)
    } yield Ok(Json.toJson(byId))
  }

  def create = Action.async { (request: Request[AnyContent]) =>
    request.body.asJson.map { (json: JsValue) =>
      json.validate[Media] match {
        case JsSuccess(media: Media, _) => mediaDao.insert(media); Future{Ok("inserted media item")}
        case JsError(e) => Future{ BadRequest(s"Bad payload: $e") }
      }
    }.getOrElse(Future{BadRequest("Unable to parse payload")})
  }

  def delete(mediaId: Long) = Action.async {
    Future{
    }.flatMap { _ =>
      subtitleDao.deleteAll(mediaId)
    }.flatMap{ _ =>
      mediaDao.delete(mediaId)
    }.flatMap{ _ =>
      Future(Ok(s"Media with id $mediaId deleted"))
    }
  }

  def update(mediaId: Long) = Action.async { request =>
    request.body.asJson.map( json =>
      json.validate[Media] match {
        case JsSuccess(media, _) => mediaDao.update(mediaId, media); Future{Ok(s"media item $mediaId was successfully updated")}
        case JsError(e) => Future{ BadRequest(s"Bad payload: $e")}
      }
    ).getOrElse(Future{BadRequest("Unable to parse payload")})
  }

}
