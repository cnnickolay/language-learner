package controllers

import com.google.inject.Inject
import model.MediaGroupDao
import model.Model._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MediaGroupController @Inject()(mediaGroupDao: MediaGroupDao) extends Controller {

  def getAll = Action.async {
    mediaGroupDao.all().map(all => Ok(Json.toJson(all)))
  }

  def byId(mediaGroupId: Long) = Action.async {
    mediaGroupDao.byId(mediaGroupId).map(all => Ok(Json.toJson(all)))
  }

  def create = Action.async {
    Future(Ok(""))
  }

}
