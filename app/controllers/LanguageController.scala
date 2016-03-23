package controllers

import com.google.inject.Inject
import model.LanguageDao
import model.JsonConverters._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global

class LanguageController @Inject()(languageDao: LanguageDao) extends Controller {

  def getAll = Action.async {
    for {
      languages <- languageDao.all()
    } yield Ok(Json.toJson(languages))
  }

  def byId(id: Int) = Action.async {
    for {
      language <- languageDao.byId(id)
    } yield {
      language match {
        case Some(value) => Ok(Json.toJson(value))
        case None => BadRequest(s"Language with id $id not found")
      }
    }
  }

}
