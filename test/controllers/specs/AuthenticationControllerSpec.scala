package controllers.specs

import controllers.{TestSupport, WithLangApplication}
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
class AuthenticationControllerSpec extends Specification with TestSupport with ScalaFutures {

  "GET /auth" should {
    "refresh token timestamp" in new WithLangApplication(app) {
      import dbProvider.driver.api._

      val token = godRoleUserAuthToken
      val user = godRoleUser
      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += user,
          authTokenDao.AuthTokens += token
        )), Inf)

      minutesPassed(10)

      val result = route(app, FakeRequest(GET, "/auth", FakeHeaders().add(tokenHeader(token.token)), "")).get
      status(result) must equalTo(OK)

      whenReady(authTokenDao.findToken(firstAuthToken)) {
        val expectedAuthToken = token.copy(expiresAt = presentTime.plusMinutes(user.sessionDuration))
        _ must equalTo(Some(expectedAuthToken))
      }
    }
  }

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

      val user = godRoleUser
      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += user
        )
      ), Inf)

      val result = route(app, FakeRequest(POST, "/auth", FakeHeaders().add(jsonContentTypeHeader), request.toString)).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      (contentAsJson(result) \ "token").as[String] must equalTo(s"$firstAuthToken")
      (contentAsJson(result) \ "expiresAt").as[Long] must equalTo(presentTime.plusMinutes(user.sessionDuration).getMillis)
    }

    "successful authentication, existing token revoked due to expiration, new one granted" in new WithLangApplication(app) {
      import dbProvider.driver.api._

      val token = godRoleUserAuthToken.copy(expiresAt = presentTime.plusMinutes(20))
      val user = godRoleUser.copy(sessionDuration = 10)
      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += user,
          authTokenDao.AuthTokens += token
        )), Inf)

      minutesPassed(60)

      val result = route(app, FakeRequest(POST, "/auth", FakeHeaders().add(jsonContentTypeHeader), request.toString)).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      (contentAsJson(result) \ "token").as[String] must equalTo(s"$secondAuthToken")

      whenReady(authTokenDao.findToken(firstAuthToken))
      {
        val expectedAuthToken = token.copy(expiredAt = Some(presentTime), active = false)
        _ must equalTo(Some(expectedAuthToken))
      }
      whenReady(authTokenDao.findToken(secondAuthToken))
      {
        val expectedAuthToken = token.copy(token = secondAuthToken, createdAt = presentTime,
          expiresAt = presentTime.plusMinutes(user.sessionDuration), active = true)
        _ must equalTo(Some(expectedAuthToken))
      }
    }

    "successful authentication, reusing token" in new WithLangApplication(app) {
      import dbProvider.driver.api._

      val authToken = godRoleUserAuthToken
      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += godRoleUser,
          authTokenDao.AuthTokens += authToken
        )), Inf)

      minutesPassed(20)

      val result = route(app, FakeRequest(POST, "/auth", FakeHeaders().add(jsonContentTypeHeader), request.toString)).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      (contentAsJson(result) \ "token").as[String] must equalTo(s"${authToken.token}")

      whenReady(authTokenDao.findToken(authToken.token))
      {
        val expectedAuthToken = authToken.copy(expiresAt = presentTime.plusMinutes(godRoleUser.sessionDuration))
        _ must equalTo(Some(expectedAuthToken))
      }
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

      val authToken = godRoleUserAuthToken
      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += godRoleUser,
          authTokenDao.AuthTokens += authToken
        )), Inf)


      val result = route(app, FakeRequest(DELETE, "/auth", FakeHeaders().add(tokenHeader(authToken.token)), "")).get

      status(result) must equalTo(OK)
      contentAsString(result) must equalTo("Token has been revoked")
    }

    "do nothing if wrong token provided" in new WithLangApplication(app) {
      import dbProvider.driver.api._

      val authToken = godRoleUserAuthToken
      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += godRoleUser,
          authTokenDao.AuthTokens += authToken.copy(active = false)
        )), Inf)

      val result = route(app, FakeRequest(DELETE, "/auth", FakeHeaders().add(tokenHeader(authToken.token)), "")).get

      status(result) must equalTo(UNAUTHORIZED)
    }

  }
}
