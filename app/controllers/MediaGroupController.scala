package controllers

import com.google.inject.Inject
import model.{MediaGroup, MediaGroupDao}
import model.JsonConverters._
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}
import utils.actions.{AuthTokenRefreshAction, UserAction, ActionsConfiguration, CORSAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MediaGroupController @Inject()(val mediaGroupDao: MediaGroupDao,
                                     val userAction: UserAction,
                                     val authTokenRefreshAction: AuthTokenRefreshAction) extends Controller with ActionsConfiguration {

  def getAll = userActionWithCORS.async {
    mediaGroupDao.all().map(all => Ok(Json.toJson(all)))
  }

  def byId(mediaGroupId: Long) = Action.async {
    mediaGroupDao.byId(mediaGroupId).map(all => Ok(Json.toJson(all)))
  }

  def create = Action.async { request =>
    Future {
      request.body.asJson.map { json =>
        json.validate[MediaGroup] match {
          case JsSuccess(value, _) => mediaGroupDao.insert(value); Ok("New media group added successfully")
          case JsError(e) => BadRequest("Failed to parse json")
        }
      }.getOrElse(BadRequest("Failed to process request"))
    }
  }

  def delete(mediaGroupId: Long) = Action.async {
    for {
      _ <- mediaGroupDao.delete(mediaGroupId)
    } yield Ok(s"Media group $mediaGroupId has been deleted")
  }

}
