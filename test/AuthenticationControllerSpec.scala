import java.sql.Timestamp
import java.util.UUID

import com.google.inject.Inject
import model._
import org.joda.time.DateTime
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import play.api.{mvc, inject}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsLookupResult, JsValue}
import play.api.test._
import play.test
import play.api.test._
import play.api.test.Helpers._
import play.libs.Json
import slick.backend.DatabaseConfig
import slick.dbio
import slick.dbio.Effect.{Schema, Write}
import slick.dbio.{NoStream, DBIOAction, Effect}
import slick.driver.JdbcProfile
import utils.{AuthTokenGenerator, DefaultAuthTokenGenerator}
import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.duration.Duration.Inf

@RunWith(classOf[JUnitRunner])
class AuthenticationControllerSpec extends Specification with TestSupport {

  "POST /auth" should {
    val request = Json.parse {
      """
        |{
        |  "login": "niko",
        |  "password": "test"
        |}
      """.stripMargin
    }
    "successful authentication, creating token" in new WithLangApplication(app) {
      import dbProvider.driver.api._

      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += godRoleUser
        )
      ), Inf)

      val result = route(app, FakeRequest(POST, "/auth", FakeHeaders().add(jsonContentTypeHeader), request.toString)).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      (contentAsJson(result) \ "token").get.toString must equalTo(s""""${AuthTokenGeneratorMock.nextToken}"""")
    }

    "successful authentication, reusing token" in new WithLangApplication(app) {
      import dbProvider.driver.api._

      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += godRoleUser,
          authTokenDao.AuthTokens += godRoleUserAuthToken
        )), Inf)

      val result = route(app, FakeRequest(POST, "/auth", FakeHeaders().add(jsonContentTypeHeader), request.toString)).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      (contentAsJson(result) \ "token").get.toString must equalTo(s""""${godRoleUserAuthToken.token}"""")
    }

    "authentication failed due to wrong login/password" in new WithLangApplication(app) {
      val result = route(app, FakeRequest(POST, "/auth", FakeHeaders().add(jsonContentTypeHeader), request.toString)).get

      status(result) must equalTo(UNAUTHORIZED)
      contentAsString(result) must equalTo("User with login niko and password hash 9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08 not found")
    }

    "authentication failed due to bad json" in new WithLangApplication(app) {
      val badJsonRequest = "{bad json"
      val result = route(app, FakeRequest(POST, "/auth", FakeHeaders().add(jsonContentTypeHeader), badJsonRequest)).get

      status(result) must equalTo(BAD_REQUEST)
    }
  }

  "DELETE /auth" should {
    "allow user to log out" in new WithLangApplication(app) {
      import dbProvider.driver.api._

      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += godRoleUser,
          authTokenDao.AuthTokens += godRoleUserAuthToken
        )), Inf)


      val result = route(app, FakeRequest(DELETE, "/auth", FakeHeaders().add(tokenHeader(godRoleUserAuthToken.token)), "")).get

      status(result) must equalTo(OK)
      contentAsString(result) must equalTo("Token has been revoked")
    }

    "do nothing if wrong token provided" in new WithLangApplication(app) {
      import dbProvider.driver.api._

      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += godRoleUser,
          authTokenDao.AuthTokens += godRoleUserAuthToken.copy(active = false)
        )), Inf)

      val result = route(app, FakeRequest(DELETE, "/auth", FakeHeaders().add(tokenHeader(godRoleUserAuthToken.token)), "")).get

      status(result) must equalTo(UNAUTHORIZED)
    }

  }
}
