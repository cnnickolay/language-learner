package controllers

import com.google.inject.Inject
import model.MediaDao
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._
import model.Model._

import scala.concurrent.Future

class Application @Inject()(mediaDao: MediaDao) extends Controller {

  def index = Action.async {
    for {
      _ <- mediaDao.delete(1)
      _ <- mediaDao.insert(Media(id = None, name = "med1"))
      medias <- mediaDao.all()
    } yield Ok(Json.toJson(medias))
  }

}
