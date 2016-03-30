package controllers

import com.google.inject.Inject
import model.LanguageDao
import model.JsonConverters._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils.actions.{UserAction, AuthTokenRefreshAction, ActionsConfiguration}

import scala.concurrent.ExecutionContext.Implicits.global

class LanguageController @Inject()(val languageDao: LanguageDao,
                                   val userAction: UserAction,
                                   val authTokenRefreshAction: AuthTokenRefreshAction) extends Controller with ActionsConfiguration {

  def getAll = authActionWithCORS.async {
    for {
      languages <- languageDao.all()
    } yield Ok(Json.toJson(languages))
  }

  def byId(id: Int) = authActionWithCORS.async {
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
