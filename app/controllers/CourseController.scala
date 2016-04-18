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

  case class CourseJson(id: Option[Long] = None, name: String, targetLanguage: String, presentingLanguage: String)

  implicit val courseJsonFormatter = Json.format[CourseJson]

  def getAll = authAction.async {
    languageDao.all()
      .flatMap { languages =>
        courseDao.all().map { courses =>
          courses.map { course =>
            val targetLanguage = languages.find(_.id.getOrElse(-1) == course.targetLanguageId).map(_.name).getOrElse("")
            val presentingLanguage = languages.find(_.id.getOrElse(-1) == course.presentingLanguageId).map(_.name).getOrElse("")
            CourseJson(course.id, course.name, targetLanguage, presentingLanguage)
          }
        }
      }.map { courseJsonSeq =>
        Ok(Json.toJson(courseJsonSeq))
      }
  }

  def create = adminAction.async { request =>
    def saveCourse(course: CourseJson): Option[Future[Status]] = {
      languageByName(course.targetLanguage) zip languageByName(course.presentingLanguage) map {
        case (targetLanguage, presentingLanguage) =>
          courseDao.byName(course.name).flatMap {
            case None => courseDao.insert(Course(None, course.name, targetLanguage.id, presentingLanguage.id)) map { _ => Ok }
            case _ => Future(BadRequest)
          }

      } headOption
    }

    request.body.asJson.flatMap { json =>
      json.validate[CourseJson] match {
        case JsSuccess(course, _) => saveCourse(course)
        case JsError(_) => Some(Future(BadRequest("Wrong json format")))
      }
    }.getOrElse(Future(BadRequest("Wrong payload, expected json")))
  }

  def update(courseId: Long) = adminAction.async { request =>
    def processJsonRequest(json: JsValue): Future[Result] = {
      json.validate[CourseJson] match {
        case JsSuccess(newCourseJson, _) =>
          languageByName(newCourseJson.targetLanguage) zip languageByName(newCourseJson.presentingLanguage) map {
            case (targetLanguage, presentingLanguage) =>
              courseDao.byId(courseId)
                .flatMap {
                  case Some(existingCourse) =>
                    val newCourse = new Course(Some(courseId), newCourseJson.name, targetLanguage.id, presentingLanguage.id)
                    courseDao.update(courseId, newCourse).map(_ => Ok)
                  case _ => Future(BadRequest(s"No course with id $courseId found"))
                }
          } headOption match {
            case None => Future(BadRequest("Languages provided are wrong"))
            case Some(res) => res
          }
        case JsError(_) => Future(BadRequest("Wrong json format"))
      }
    }

    request.body.asJson.map { json =>
      processJsonRequest(json)
    }.getOrElse(Future(BadRequest("Wrong payload, expected json")))
  }

  def delete(courseId: Long) = adminAndGodAction.async {
    courseDao.delete(courseId).map(_ => Ok)
  }

}
