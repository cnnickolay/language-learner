package controllers

import com.google.inject.Inject
import model._
import play.api.libs.json._
import play.api.mvc.{Controller, Result}
import utils.actions.{ActionsConfiguration, AuthTokenRefreshAction, UserAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CourseController @Inject()(val languageDao: LanguageDao,
                                 val courseDao: CourseDao,
                                 val userAction: UserAction,
                                 val authTokenRefreshAction: AuthTokenRefreshAction) extends Controller with ActionsConfiguration {

  case class CourseJson(id: Long, name: String, targetLanguage: String, presentingLanguage: String)

  implicit val courseJsonFormatter = Json.format[CourseJson]

  def getAll = authAction.async {
    languageDao.all()
      .flatMap { languages =>
        courseDao.all().map { courses =>
          courses.map { course =>
            val targetLanguage = languages.find(_.id.getOrElse(-1) == course.targetLanguageId).map(_.name).getOrElse("")
            val presentingLanguage = languages.find(_.id.getOrElse(-1) == course.presentingLanguageId).map(_.name).getOrElse("")
            CourseJson(course.id.getOrElse(-1), course.name, targetLanguage, presentingLanguage)
          }
        }
      }.map { courseJsonSeq =>
        Ok(Json.toJson(courseJsonSeq))
      }
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
