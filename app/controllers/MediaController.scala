package controllers

import com.google.inject.Inject
import model.MediaDao
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import model.Model._

import scala.concurrent.Future

class MediaController @Inject()(mediaDao: MediaDao) extends Controller {

  def getAll = Action.async {
    for {
      _ <- mediaDao.insert(Media(id = None, name = "med1"))
      medias <- mediaDao.all()
    } yield Ok(Json.toJson(medias))
  }

  def create = Action.async { request =>
    val jsonRequest: JsValue = request.body.asJson.get
    val x: Media = jsonRequest.validate[Media] match {
      case JsSuccess(x: Media, _) => x
    }

    for {
      _ <- mediaDao.insert(x)
    } yield Ok("")
  }

}
