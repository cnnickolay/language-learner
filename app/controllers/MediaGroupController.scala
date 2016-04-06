package controllers

import com.google.inject.Inject
import model.{MediaGroup, MediaGroupDao}
import model.JsonConverters._
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}
import utils.actions.{AuthTokenRefreshAction, UserAction, ActionsConfiguration, CORSAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MediaGroupController @Inject()(val mediaGroupDao: MediaGroupDao,
                                     val userAction: UserAction,
                                     val authTokenRefreshAction: AuthTokenRefreshAction) extends Controller with ActionsConfiguration {

  val l = Logger(classOf[MediaGroupController])

  def getAll = authActionWithCORS.async {
    mediaGroupDao.all().map(all => Ok(Json.toJson(all)))
  }

  def byId(mediaGroupId: Long) = authActionWithCORS.async {
    mediaGroupDao.byId(mediaGroupId).map {
      case Some(mediaGroup) => Ok(Json.toJson(mediaGroup))
      case None => BadRequest(s"Media group with id $mediaGroupId does not exist")
    }
  }

  def create = authActionWithCORS.async { request =>
    Future {
      request.body.asJson.map { json =>
        json.validate[MediaGroup] match {
          case JsSuccess(value, _) => mediaGroupDao.insert(value); Ok("New media group added successfully")
          case JsError(e) => l.debug("Failed to parse json"); BadRequest("Failed to parse json")
        }
      }.getOrElse(BadRequest("Failed to process request"))
    }
  }

  def delete(mediaGroupId: Long) = authActionWithCORS.async {
    for {
      _ <- mediaGroupDao.delete(mediaGroupId)
    } yield Ok(s"Media group $mediaGroupId has been deleted")
  }

}
