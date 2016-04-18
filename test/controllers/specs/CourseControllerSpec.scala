package controllers.specs

import controllers.{FakeDataGen, TestSupport, WithLangApplication}
import model._
import org.junit.runner._
import org.scalatest.concurrent.ScalaFutures
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json.Json
import play.api.test.{FakeHeaders, FakeRequest}
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class CourseControllerSpec extends Specification with TestSupport with ScalaFutures with FakeDataGen {

  val godAuthToken = authTokenGenerator.nextAuthToken
  val adminAuthToken = authTokenGenerator.nextAuthToken
  val teacherAuthToken = authTokenGenerator.nextAuthToken
  val studentAuthToken = authTokenGenerator.nextAuthToken

  val insertGodUser = insertUserWithAuthToken(999, godAuthToken, GodRoleEnum, "_god")
  val insertAdminUser = insertUserWithAuthToken(100, adminAuthToken, AdminRoleEnum, "admin")
  val insertTeacherUser = insertUserWithAuthToken(200, teacherAuthToken, TeacherRoleEnum, "teacher")
  val insertStudentUser = insertUserWithAuthToken(300, studentAuthToken, StudentRoleEnum, "student")

  "GET /course" should {

    "return all courses if user is ADMIN" in new WithLangApplication(app) {
      override def sqlTestData: Seq[String] =
        insertAdminUser :+
        insertCourse(1, "course1", FrenchLanguageEnum.id, EnglishLanguageEnum.id) :+
        insertCourse(2, "course2", EnglishLanguageEnum.id, GermanLanguageEnum.id)

      val result = route(app, FakeRequest(GET, "/course", FakeHeaders().add(tokenHeader(adminAuthToken)), "")).get
      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      contentAsJson(result) should equalTo(Json.parse(
        s"""
           |[
           |  {
           |    "id": 1,
           |    "name": "course1",
           |    "targetLanguage": "french",
           |    "presentingLanguage": "english"
           |  },
           |  {
           |    "id": 2,
           |    "name": "course2",
           |    "targetLanguage": "english",
           |    "presentingLanguage": "german"
           |  }
           |]
        """.stripMargin))

    }

    "return 401 if user not authenticated" in new WithLangApplication(app) {
      val result = route(app, FakeRequest(GET, "/course", FakeHeaders(), "")).get
      status(result) must equalTo(UNAUTHORIZED)
    }
  }

  "POST /course" should {
    "create new course entry if user is ADMIN" in new WithLangApplication(app) {
      lazy val courseName: String = s"${generateName}Course"
      val request =
        s"""
          |{
          |  "name": "$courseName",
          |  "targetLanguage": "english",
          |  "presentingLanguage": "french"
          |}
        """.stripMargin

      override def sqlTestData: Seq[String] = insertAdminUser

      whenReady(courseDao.byName(courseName)) {
        _ must equalTo(None)
      }

      val result = route(app, FakeRequest(POST, "/course", FakeHeaders().add(tokenHeader(adminAuthToken)).add(jsonContentTypeHeader), request)).get
      status(result) must equalTo(OK)
      whenReady(courseDao.byName(courseName)) {
        case Some(course) =>
          course.name must equalTo(courseName)
          course.targetLanguageId must equalTo(EnglishLanguageEnum.id)
          course.presentingLanguageId must equalTo(FrenchLanguageEnum.id)
        case _ => failure("course object not found")
      }
    }

    "fail to create new course entry if target language provided is wrong" in new WithLangApplication(app) {
      lazy val courseName: String = s"${generateName}Course"
      val request =
        s"""
          |{
          |  "name": "$courseName",
          |  "targetLanguage": "blablabla",
          |  "presentingLanguage": "french"
          |}
        """.stripMargin

      override def sqlTestData: Seq[String] = insertAdminUser

      whenReady(courseDao.byName(courseName)) {
        _ must equalTo(None)
      }

      val result = route(app, FakeRequest(POST, "/course", FakeHeaders().add(tokenHeader(adminAuthToken)).add(jsonContentTypeHeader), request)).get
      status(result) must equalTo(BAD_REQUEST)
      whenReady(courseDao.byName(courseName)) {
        _ must equalTo(None)
      }
    }

    "return 400 if course with given name already exists" in new WithLangApplication(app) {
      lazy val courseName: String = s"${generateName}Course"
      val request =
        s"""
           |{
           |  "name": "$courseName",
           |  "targetLanguage": "english",
           |  "presentingLanguage": "french"
           |}
        """.stripMargin

      override def sqlTestData: Seq[String] = insertAdminUser :+ insertCourse(1, s"$courseName", EnglishLanguageEnum.id, FrenchLanguageEnum.id)

      whenReady(courseDao.byName(courseName)) {
        _ must not equalTo None
      }

      val result = route(app, FakeRequest(POST, "/course", FakeHeaders().add(tokenHeader(adminAuthToken)).add(jsonContentTypeHeader), request)).get
      status(result) must equalTo(BAD_REQUEST)
    }

    "return 401 if user not authenticated" in new WithLangApplication(app) {
      val result = route(app, FakeRequest(GET, "/course", FakeHeaders(), "")).get
      status(result) must equalTo(UNAUTHORIZED)
    }

    "return 403 if user is TEACHER" in new WithLangApplication(app) {
      override def sqlTestData: Seq[String] = insertTeacherUser
      val result = route(app, FakeRequest(POST, "/course", FakeHeaders().add(tokenHeader(teacherAuthToken)), "")).get
      status(result) must equalTo(FORBIDDEN)
    }

    "return 403 if user is STUDENT" in new WithLangApplication(app) {
      override def sqlTestData: Seq[String] = insertStudentUser
      val result = route(app, FakeRequest(POST, "/course", FakeHeaders().add(tokenHeader(studentAuthToken)), "")).get
      status(result) must equalTo(FORBIDDEN)
    }
  }

  "PUT /course/:id" should {
    "update existing course entry if user is ADMIN" in new WithLangApplication(app) {
      lazy val courseName: String = "oldCourse"
      lazy val newCourseName: String = "newCourse"
      val request =
        s"""
           |{
           |  "name": "$newCourseName",
           |  "targetLanguage": "english",
           |  "presentingLanguage": "french"
           |}
        """.stripMargin

      override def sqlTestData: Seq[String] = insertAdminUser :+ insertCourse(1, courseName, FrenchLanguageEnum.id, GermanLanguageEnum.id)

      whenReady(courseDao.byName(courseName)) {
        case Some(course) =>
          course.targetLanguageId must equalTo(FrenchLanguageEnum.id)
          course.presentingLanguageId must equalTo(GermanLanguageEnum.id)
        case _ => failure(s"course object $courseName not found")
      }
      whenReady(courseDao.byName(newCourseName)) {
        _ must equalTo(None)
      }

      val result = route(app, FakeRequest(PUT, "/course/1", FakeHeaders().add(tokenHeader(adminAuthToken)).add(jsonContentTypeHeader), request)).get
      status(result) must equalTo(OK)
      whenReady(courseDao.byName(newCourseName)) {
        case Some(course) =>
          course.targetLanguageId must equalTo(EnglishLanguageEnum.id)
          course.presentingLanguageId must equalTo(FrenchLanguageEnum.id)
        case _ => failure(s"course object $newCourseName not found")
      }
      whenReady(courseDao.byName(courseName)) {
        _ must equalTo(None)
      }
    }

    "return 400 if course with given name already exists" in new WithLangApplication(app) {
      lazy val courseName: String = "oldCourse"
      lazy val newCourseName: String = "newCourse"
      val request =
        s"""
           |{
           |  "name": "$newCourseName",
           |  "targetLanguage": "english",
           |  "presentingLanguage": "french"
           |}
        """.stripMargin

      override def sqlTestData: Seq[String] = insertAdminUser :+ insertCourse(1, courseName, FrenchLanguageEnum.id, GermanLanguageEnum.id)

      whenReady(courseDao.byName(courseName)) {
        case Some(course) =>
          course.targetLanguageId must equalTo(FrenchLanguageEnum.id)
          course.presentingLanguageId must equalTo(GermanLanguageEnum.id)
        case _ => failure(s"course object $courseName not found")
      }
      whenReady(courseDao.byName(newCourseName)) {
        _ must equalTo(None)
      }

      val result = route(app, FakeRequest(PUT, "/course/1", FakeHeaders().add(tokenHeader(adminAuthToken)).add(jsonContentTypeHeader), request)).get
      status(result) must equalTo(OK)
    }

    "return 401 if user not authenticated" in new WithLangApplication(app) {
      val result = route(app, FakeRequest(GET, "/course", FakeHeaders(), "")).get
      status(result) must equalTo(UNAUTHORIZED)
    }

    "return 403 if user is TEACHER" in new WithLangApplication(app) {
      override def sqlTestData: Seq[String] = insertTeacherUser
      val result = route(app, FakeRequest(PUT, "/course/1", FakeHeaders().add(tokenHeader(teacherAuthToken)), "")).get
      status(result) must equalTo(FORBIDDEN)
    }

    "return 403 if user is STUDENT" in new WithLangApplication(app) {
      override def sqlTestData: Seq[String] = insertStudentUser
      val result = route(app, FakeRequest(PUT, "/course/1", FakeHeaders().add(tokenHeader(studentAuthToken)), "")).get
      status(result) must equalTo(FORBIDDEN)
    }
  }

  "DELETE /course/:id" should {
    "delete existing course entry if user is ADMIN and course exists" in new WithLangApplication(app) {
      override def sqlTestData: Seq[String] =
        insertGodUser :+
          insertCourse(1, "course1", FrenchLanguageEnum.id, EnglishLanguageEnum.id) :+
          insertCourse(2, "course2", EnglishLanguageEnum.id, GermanLanguageEnum.id)

      whenReady(courseDao.byId(1)) {
        _ must not equalTo None
      }

      val result = route(app, FakeRequest(DELETE, "/course/1", FakeHeaders().add(tokenHeader(godAuthToken)), "")).get
      status(result) must equalTo(OK)
      whenReady(courseDao.byId(1)) {
        _ must equalTo(None)
      }
    }

    "delete existing course entry if user is GOD and course exists" in new WithLangApplication(app) {
      override def sqlTestData: Seq[String] =
        insertGodUser :+
        insertCourse(1, "course1", FrenchLanguageEnum.id, EnglishLanguageEnum.id) :+
        insertCourse(2, "course2", EnglishLanguageEnum.id, GermanLanguageEnum.id)

      whenReady(courseDao.byId(1)) {
        _ must not equalTo None
      }

      val result = route(app, FakeRequest(DELETE, "/course/1", FakeHeaders().add(tokenHeader(godAuthToken)), "")).get
      status(result) must equalTo(OK)
      whenReady(courseDao.byId(1)) {
        _ must equalTo(None)
      }
    }

    "return 401 if user not authenticated" in new WithLangApplication(app) {
      val result = route(app, FakeRequest(GET, "/course", FakeHeaders(), "")).get
      status(result) must equalTo(UNAUTHORIZED)
    }

    "return 403 if user is TEACHER" in new WithLangApplication(app) {
      override def sqlTestData: Seq[String] = insertTeacherUser
      val result = route(app, FakeRequest(DELETE, "/course/1", FakeHeaders().add(tokenHeader(teacherAuthToken)), "")).get
      status(result) must equalTo(FORBIDDEN)
    }

    "return 403 if user is STUDENT" in new WithLangApplication(app) {
      override def sqlTestData: Seq[String] = insertStudentUser
      val result = route(app, FakeRequest(DELETE, "/course/1", FakeHeaders().add(tokenHeader(studentAuthToken)), "")).get
      status(result) must equalTo(FORBIDDEN)
    }
  }

}
