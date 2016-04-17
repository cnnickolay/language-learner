package controllers.specs

import controllers.{TestSupport, WithLangApplication}
import model._
import org.junit.runner._
import org.scalatest.concurrent.ScalaFutures
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._
import play.libs.Json
import slick.dbio

import scala.concurrent.Await
import scala.concurrent.duration.Duration.Inf

@RunWith(classOf[JUnitRunner])
class CourseControllerSpec extends Specification with TestSupport with ScalaFutures {

  lazy val adminAuthToken = authTokenGenerator.nextAuthToken
  lazy val teacherAuthToken = authTokenGenerator.nextAuthToken
  lazy val studentAuthToken = authTokenGenerator.nextAuthToken

  val insertAdminUser = insertUser(100, adminAuthToken, AdminRoleEnum, "admin")
  val insertTeacherUser = insertUser(200, teacherAuthToken, TeacherRoleEnum, "teacher")
  val insertStudentUser = insertUser(300, studentAuthToken, StudentRoleEnum, "student")

  "GET /course" should {

    "return all courses if user is ADMIN" in new WithLangApplication(app) {
      override def sqlTestData: Seq[String] = insertAdminUser

      val result = route(app, FakeRequest(GET, "/course", FakeHeaders().add(tokenHeader(adminAuthToken)), "")).get
      status(result) must equalTo(OK)
    }

    "return 401 if user not authenticated" in new WithLangApplication(app) {
      val result = route(app, FakeRequest(GET, "/course", FakeHeaders(), "")).get
      status(result) must equalTo(UNAUTHORIZED)
    }
  }

  "POST /course" should {
    "create new course entry if user is ADMIN" in new WithLangApplication(app) { failure }

    "create new course entry if user is GOD" in new WithLangApplication(app) { failure }

    "return 400 if course with given name already exists" in new WithLangApplication(app) { failure }

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
    "update existing course entry if user is ADMIN" in new WithLangApplication(app) { failure }

    "update existing course entry if user is GOD" in new WithLangApplication(app) { failure }

    "return 400 if course with given name already exists" in new WithLangApplication(app) { failure }

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
    "delete existing course entry if user is ADMIN and course exists" in new WithLangApplication(app) { failure }

    "delete existing course entry if user is GOD and course exists" in new WithLangApplication(app) { failure }

    "return 400 if course with given id does not exist" in new WithLangApplication(app) { failure }

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
