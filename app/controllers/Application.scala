package controllers

import com.google.inject.Inject
import model.MediaDao
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._
import model.Model._

import scala.concurrent.Future

class Application @Inject()(mediaDao: MediaDao) extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}
