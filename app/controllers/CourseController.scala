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
    val result = for {
      languages <- languageDao.all()
      courses <- courseDao.all()
    } yield courses.map { course =>
      val targetLanguage = languages.find(_.id == course.targetLanguageId).map(_.name).getOrElse("")
      val presentingLanguage = languages.find(_.id == course.presentingLanguageId).map(_.name).getOrElse("")
      CourseJson(course.id, course.name, targetLanguage, presentingLanguage)
    }

    result.map { courseJsonSeq =>
      Ok(Json.toJson(courseJsonSeq))
    }
  }

  def create = adminAction.async { request =>
    request.body.asJson.flatMap { json =>
      json.validate[CourseJson] match {
        case JsSuccess(course, _) =>
          for {
            targetLanguage <- languageByName(course.targetLanguage)
            presentingLanguage <- languageByName(course.presentingLanguage)
          } yield courseDao.byName(course.name).map {
            case None => courseDao.insert(Course(None, course.name, targetLanguage.id, presentingLanguage.id)); Ok
            case _ => BadRequest
          }
        case JsError(_) => Some(Future(BadRequest("Wrong json format")))
      }
    }.getOrElse(Future(BadRequest("Wrong payload, expected json")))
  }

  def update(courseId: Long) = adminAction.async { request =>
    def processJsonRequest(json: JsValue): Future[Result] = {
      json.validate[CourseJson] match {
        case JsSuccess(newCourseJson, _) =>
          val result = for {
            targetLanguage <- languageByName(newCourseJson.targetLanguage)
            presentingLanguage <- languageByName(newCourseJson.presentingLanguage)
          } yield for {
            Some(course) <- courseDao.byId(courseId)
            existingCourse <- courseDao.byName(newCourseJson.name)
          } yield existingCourse match {
            case None =>
              courseDao.update(courseId, new Course(Some(courseId), newCourseJson.name, targetLanguage.id, presentingLanguage.id))
              Ok(s"Course $courseId was updated")
            case _ => BadRequest("Course with this name already exists")
          }
          result match {
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
    courseDao.delete(courseId)
    Future(Ok)
  }

}
