package controllers

import com.google.inject.Inject
import model.{CourseDao, LanguageDao}
import model.JsonConverters._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils.actions.{UserAction, AuthTokenRefreshAction, ActionsConfiguration}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CourseController @Inject()(val courseDao: CourseDao,
                                 val userAction: UserAction,
                                 val authTokenRefreshAction: AuthTokenRefreshAction) extends Controller with ActionsConfiguration {



  def getAll = authAction.async {
    Future(Ok)
  }

  def create = adminAction.async { request =>
    Future(Ok)
  }

  def update(courseId: Long) = adminAction.async { request =>
    Future(Ok)
  }

  def delete(courseId: Long) = adminAction.async { request =>
    Future(Ok)
  }

}
