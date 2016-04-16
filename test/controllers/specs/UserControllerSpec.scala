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

  "GET /users" should {
    lazy val teacherAuthToken = authTokenGenerator.nextAuthToken
    lazy val otherTeacherAuthToken = authTokenGenerator.nextAuthToken
    lazy val godAuthToken = authTokenGenerator.nextAuthToken

    val defaultData = Seq(
      s"""INSERT INTO "user" (id, name, lastname, login, password_hash, status_id, session_duration, role_id, owner_user_id) VALUES
          (100, 'teacher', NULL, 'teacher', '$SHA256_123', ${ActiveEnum.id}, 1440, ${TeacherRoleEnum.id}, NULL),
          (101, 'student1', NULL, 'student1', '$SHA256_123', ${ActiveEnum.id}, 1440, ${StudentRoleEnum.id}, 100),
          (102, 'student2', NULL, 'student2', '$SHA256_123', ${ActiveEnum.id}, 1440, ${StudentRoleEnum.id}, 100),
          (200, 'otherteacher', NULL, 'otherteacher', '$SHA256_123', ${ActiveEnum.id}, 1440, ${TeacherRoleEnum.id}, NULL),
          (201, 'otherstudent1', NULL, 'otherstudent1', '$SHA256_123', ${ActiveEnum.id}, 1440, ${StudentRoleEnum.id}, 200),
          (202, 'otherstudent2', NULL, 'otherstudent2', '$SHA256_123', ${ActiveEnum.id}, 1440, ${StudentRoleEnum.id}, 200),
          (300, '_god', NULL, '_god', '$SHA256_123', ${ActiveEnum.id}, 1440, ${GodRoleEnum.id}, NULL);
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
           |    "login": "student1"
           |  },
           |  {
           |    "id": 102,
           |    "login": "student2"
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
           |    "login": "otherstudent1"
           |  },
           |  {
           |    "id": 202,
           |    "login": "otherstudent2"
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
           |    "login": "god"
           |  },
           |  {
           |    "id": 100,
           |    "login": "teacher"
           |  },
           |  {
           |    "id": 101,
           |    "login": "student1"
           |  },
           |  {
           |    "id": 102,
           |    "login": "student2"
           |  },
           |  {
           |    "id": 200,
           |    "login": "otherteacher"
           |  },
           |  {
           |    "id": 201,
           |    "login": "otherstudent1"
           |  },
           |  {
           |    "id": 202,
           |    "login": "otherstudent2"
           |  },
           |  {
           |    "id": 300,
           |    "login": "_god"
           |  }
           |]
        """.stripMargin))
    }

  }

}
