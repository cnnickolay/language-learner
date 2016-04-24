package controllers

import com.google.inject.Inject
import model.{MediaGroup, MediaGroupDao}
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.Controller
import utils.actions.{CORSAction, ActionsConfiguration, AuthTokenRefreshAction, UserAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MediaGroupController @Inject()(val mediaGroupDao: MediaGroupDao,
                                     val userAction: UserAction,
                                     val authTokenRefreshAction: AuthTokenRefreshAction,
                                     val corsAction: CORSAction) extends Controller with ActionsConfiguration {

  val l = Logger(classOf[MediaGroupController])

  val jsonFormatter: Format[(String, Option[String], Int)] = (
    (__ \ "name").format[String] ~
    (__ \ "description").formatNullable[String] ~
    (__ \ "languageId").format[Int]
  ).tupled

  def getAll = authAction.async {
    mediaGroupDao.all().map { all =>
      Ok {
        all.foldLeft(Json.arr()) { (arr, mediaGroup) =>
          arr.append(jsonFormatter writes(mediaGroup.name, mediaGroup.description, mediaGroup.languageId))
        }
      }
    }
  }

  def byId(mediaGroupId: Long) = authAction.async {
    mediaGroupDao.byId(mediaGroupId).map {
      case Some(mediaGroup) => Ok(jsonFormatter writes (mediaGroup.name, mediaGroup.description, mediaGroup.languageId))
      case None => BadRequest(s"Media group with id $mediaGroupId does not exist")
    }
  }

  def create = authAction.async { request =>
    Future {
      request.body.asJson.map { json =>
        jsonFormatter reads json match {
          case JsSuccess((name, description, languageId), _) =>
            mediaGroupDao.insert(MediaGroup(name = name, description = description, languageId = languageId))
            Ok("New media group added successfully")
          case JsError(_) => BadRequest("Unable to parse json")
        }
      }.getOrElse(BadRequest("Failed to process request"))
    }
  }

  def delete(mediaGroupId: Long) = authAction.async {
    for {
      _ <- mediaGroupDao.delete(mediaGroupId)
    } yield Ok(s"Media group $mediaGroupId has been deleted")
  }

}
