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
class CourseController extends Specification with TestSupport with ScalaFutures {

  "GET /course" should {

    "return all courses if user is ADMIN" in new WithLangApplication(app) {
      lazy val adminAuthToken = authTokenGenerator.nextAuthToken

      override def sqlTestData: Seq[String] = Seq(
        s"""INSERT INTO "user" (id, name, lastname, login, password_hash, status_id, session_duration, role_id, owner_user_id) VALUES
          (100, 'admin', NULL, 'admin', '$SHA256_123', ${ActiveEnum.id}, 1440, ${AdminRoleEnum.id}, NULL);
        """,
        s"""INSERT INTO auth_token(token, created_at, expires_at, active, user_id) VALUES
           ('$adminAuthToken', '$presentTime', '${presentTime.plusMinutes(20)}', true, 100);
        """
      )

      val result = route(app, FakeRequest(GET, "/course", FakeHeaders().add(tokenHeader(adminAuthToken)), "")).get
      status(result) must equalTo(OK)
    }

    "return 401 if user not authenticated" in new WithLangApplication(app) { failure }
  }

  "POST /course" should {
    "create new course entry if user is ADMIN" in new WithLangApplication(app) { failure }

    "create new course entry if user is GOD" in new WithLangApplication(app) { failure }

    "return 400 if course with given name already exists" in new WithLangApplication(app) { failure }

    "return 401 if user not authenticated" in new WithLangApplication(app) { failure }

    "return 403 if user is TEACHER" in new WithLangApplication(app) { failure }

    "return 403 if user is STUDENT" in new WithLangApplication(app) { failure }
  }

  "PUT /course/:id" should {
    "update existing course entry if user is ADMIN" in new WithLangApplication(app) { failure }

    "update existing course entry if user is GOD" in new WithLangApplication(app) { failure }

    "return 400 if course with given name already exists" in new WithLangApplication(app) { failure }

    "return 401 if user not authenticated" in new WithLangApplication(app) { failure }

    "return 403 if user is TEACHER" in new WithLangApplication(app) { failure }

    "return 403 if user is STUDENT" in new WithLangApplication(app) { failure }
  }

  "DELETE /course/:id" should {
    "delete existing course entry if user is ADMIN and course exists" in new WithLangApplication(app) { failure }

    "delete existing course entry if user is GOD and course exists" in new WithLangApplication(app) { failure }

    "return 400 if course with given id does not exist" in new WithLangApplication(app) { failure }

    "return 401 if user not authenticated" in new WithLangApplication(app) { failure }

    "return 403 if user is TEACHER" in new WithLangApplication(app) { failure }

    "return 403 if user is STUDENT" in new WithLangApplication(app) { failure }
  }

}
