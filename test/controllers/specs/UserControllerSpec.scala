package controllers.specs

import controllers.{TestSupport, WithLangApplication}
import model._
import org.junit.runner._
import org.scalatest.concurrent.ScalaFutures
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json.Json
import play.api.test.{FakeHeaders, FakeRequest}
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class UserControllerSpec extends Specification with TestSupport with ScalaFutures {

  val teacherAuthToken = authTokenGenerator.nextAuthToken
  val otherTeacherAuthToken = authTokenGenerator.nextAuthToken
  val godAuthToken = authTokenGenerator.nextAuthToken

  "GET /users" should {
    val defaultData = Seq(
      s"""INSERT INTO "user" (id, name, lastname, login, password_hash, status_id, session_duration, role_id, owner_user_id) VALUES
          (100, NULL, NULL, 'teacher', '$SHA256_123', ${ActiveEnum.id}, 1440, ${TeacherRoleEnum.id}, NULL),
          (101, NULL, NULL, 'student1', '$SHA256_123', ${ActiveEnum.id}, 1440, ${StudentRoleEnum.id}, 100),
          (102, NULL, NULL, 'student2', '$SHA256_123', ${ActiveEnum.id}, 1440, ${StudentRoleEnum.id}, 100),
          (200, NULL, NULL, 'otherteacher', '$SHA256_123', ${ActiveEnum.id}, 1440, ${TeacherRoleEnum.id}, NULL),
          (201, NULL, NULL, 'otherstudent1', '$SHA256_123', ${ActiveEnum.id}, 1440, ${StudentRoleEnum.id}, 200),
          (202, NULL, NULL, 'otherstudent2', '$SHA256_123', ${ActiveEnum.id}, 1440, ${StudentRoleEnum.id}, 200),
          (300, NULL, NULL, '_god', '$SHA256_123', ${ActiveEnum.id}, 1440, ${GodRoleEnum.id}, NULL);
      """,
      s"""INSERT INTO auth_token(token, created_at, expires_at, active, user_id) VALUES
           ('$teacherAuthToken', '$presentTime', '${presentTime.plusMinutes(20)}', true, 100),
           ('$otherTeacherAuthToken', '$presentTime', '${presentTime.plusMinutes(20)}', true, 200),
           ('$godAuthToken', '$presentTime', '${presentTime.plusMinutes(20)}', true, 300);
      """
    )

    "teacher can get a list of his students" in new WithLangApplication(app) {
      override def sqlTestData = defaultData
      val result1 = route(app, FakeRequest(GET, "/users", FakeHeaders().add(tokenHeader(teacherAuthToken)), "")).get

      status(result1) should equalTo(OK)
      contentAsJson(result1) should equalTo(Json.parse(
        s"""
           |[
           |  {
           |    "id": 101,
           |    "login": "student1",
           |    "role": "student"
           |  },
           |  {
           |    "id": 102,
           |    "login": "student2",
           |    "role": "student"
           |  }
           |]
        """.stripMargin))

      val result2 = route(app, FakeRequest(GET, "/users", FakeHeaders().add(tokenHeader(otherTeacherAuthToken)), "")).get

      status(result2) should equalTo(OK)
      contentAsJson(result2) should equalTo(Json.parse(
        s"""
           |[
           |  {
           |    "id": 201,
           |    "login": "otherstudent1",
           |    "role": "student"
           |  },
           |  {
           |    "id": 202,
           |    "login": "otherstudent2",
           |    "role": "student"
           |  }
           |]
        """.stripMargin))
    }

    "god can get a list of all teachers" in new WithLangApplication(app) {
      override def sqlTestData = defaultData
      val result = route(app, FakeRequest(GET, "/users", FakeHeaders().add(tokenHeader(godAuthToken)), "")).get

      status(result) should equalTo(OK)
      contentAsJson(result) should equalTo(Json.parse(
        s"""
           |[
           |  {
           |    "id": 1,
           |    "login": "god",
           |    "name": "Nikolai",
           |    "lastname": "Cherkezishvili",
           |    "role": "god"
           |  },
           |  {
           |    "id": 100,
           |    "login": "teacher",
           |    "role": "teacher"
           |  },
           |  {
           |    "id": 101,
           |    "login": "student1",
           |    "role": "student"
           |  },
           |  {
           |    "id": 102,
           |    "login": "student2",
           |    "role": "student"
           |  },
           |  {
           |    "id": 200,
           |    "login": "otherteacher",
           |    "role": "teacher"
           |  },
           |  {
           |    "id": 201,
           |    "login": "otherstudent1",
           |    "role": "student"
           |  },
           |  {
           |    "id": 202,
           |    "login": "otherstudent2",
           |    "role": "student"
           |  },
           |  {
           |    "id": 300,
           |    "login": "_god",
           |    "role": "god"
           |  }
           |]
        """.stripMargin))
    }

  }

  "POST /users" should {

    "create new user is current user is ADMIN" in new WithLangApplication(app) {
      val request =
        s"""
          |{
          |  "login": "newTeacher",
          |  "role": "teacher",
          |  "passwordHash": "$SHA256_123"
          |}
        """.stripMargin


      override def sqlTestData: Seq[String] = insertUserWithAuthToken(100, teacherAuthToken, TeacherRoleEnum, "teacher")

      whenReady(userDao.byLogin("newTeacher")) {
        _ must equalTo(None)
      }

      val result = route(app, FakeRequest(POST, "/users", FakeHeaders().add(tokenHeader(teacherAuthToken)).add(jsonContentTypeHeader), request)).get

      contentAsString(result) must equalTo("User newTeacher was successfully added")
      status(result) must equalTo(OK)
      whenReady(userDao.byLogin("newTeacher")) {
        case Some(user) =>
          user.login must equalTo("newTeacher")
          user.roleId must equalTo(TeacherRoleEnum.id)
          user.passwordHash must equalTo(SHA256_123)
          user.ownerUserId must equalTo(Some(100))
        case None => failure("User newTeacher has not been created")
      }
    }

  }

}
