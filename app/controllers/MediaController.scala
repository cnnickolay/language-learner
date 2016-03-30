package controllers

import com.google.inject.Inject
import model.JsonConverters._
import model.{Media, MediaDao, SubtitleDao}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import utils.actions.{AuthTokenRefreshAction, UserAction, ActionsConfiguration, CORSAction}

import scala.concurrent.Future

class MediaController @Inject()(val mediaDao: MediaDao,
                                val subtitleDao: SubtitleDao,
                                val userAction: UserAction,
                                val authTokenRefreshAction: AuthTokenRefreshAction) extends Controller with ActionsConfiguration {

  def getAll = authActionWithCORS.async {
    for {
      medias <- mediaDao.all()
    } yield Ok(Json.toJson(medias))
  }

  def getAllByMediaGroup(mediaGroupId: Long) = authActionWithCORS.async {
    for {
      medias <- mediaDao.byMediaGroup(mediaGroupId)
    } yield Ok(Json.toJson(medias))
  }

  def byId(mediaId: Long) = authActionWithCORS.async {
    for {
      byId <- mediaDao.byId(mediaId)
    } yield Ok(Json.toJson(byId))
  }

  def create = authActionWithCORS.async { (request: Request[AnyContent]) =>
    request.body.asJson.map { (json: JsValue) =>
      json.validate[Media] match {
        case JsSuccess(media: Media, _) => mediaDao.insert(media); Future{Ok("inserted media item")}
        case JsError(e) => Future{ BadRequest(s"Bad payload: $e") }
      }
    }.getOrElse(Future{BadRequest("Unable to parse payload")})
  }

  def delete(mediaId: Long) = authActionWithCORS.async {
    Future{
    }.flatMap { _ =>
      subtitleDao.deleteAll(mediaId)
    }.flatMap{ _ =>
      mediaDao.delete(mediaId)
    }.flatMap{ _ =>
      Future(Ok(s"Media with id $mediaId deleted"))
    }
  }

  def update(mediaId: Long) = authActionWithCORS.async { request =>
    request.body.asJson.map( json =>
      json.validate[Media] match {
        case JsSuccess(media, _) => mediaDao.update(mediaId, media); Future{Ok(s"media item $mediaId was successfully updated")}
        case JsError(e) => Future{ BadRequest(s"Bad payload: $e")}
      }
    ).getOrElse(Future{BadRequest("Unable to parse payload")})
  }

}
