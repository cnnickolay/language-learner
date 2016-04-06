package controllers.specs

import controllers.{TestSupport, TimeServiceMock, WithLangApplication}
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

  "POST /auth" should {
    val request = Json.parse {
      """
        |{
        |  "login": "niko",
        |  "password": "test"
        |}
      """.stripMargin
    }
    "successful authentication, existing token revoked due to expiration, new one granted" in new WithLangApplication(app) {
      import dbProvider.driver.api._

      val token = godRoleUserAuthToken.copy(expiresAt = TimeServiceMock.injectedTime.plusMinutes(20))
      val user = godRoleUser.copy(sessionDuration = 10)
      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += user,
          authTokenDao.AuthTokens += token
        )), Inf)

      TimeServiceMock.injectedTime = TimeServiceMock.injectedTime.plusMinutes(60)

      val result = route(app, FakeRequest(POST, "/auth", FakeHeaders().add(jsonContentTypeHeader), request.toString)).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      (contentAsJson(result) \ "token").get.toString must equalTo(s""""$secondAuthToken"""")

      whenReady(authTokenDao.findToken(firstAuthToken))
      {
        val expectedAuthToken = token.copy(expiredAt = Some(TimeServiceMock.injectedTime), active = false)
        _ must equalTo(Some(expectedAuthToken))
      }
      whenReady(authTokenDao.findToken(secondAuthToken))
      {
        val expectedAuthToken = token.copy(token = secondAuthToken, createdAt = TimeServiceMock.injectedTime,
          expiresAt = TimeServiceMock.injectedTime.plusMinutes(user.sessionDuration), active = true)
        _ must equalTo(Some(expectedAuthToken))
      }
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
      (contentAsJson(result) \ "token").get.toString must equalTo(s""""${firstAuthToken}"""")
    }

    "successful authentication, reusing token" in new WithLangApplication(app) {
      import dbProvider.driver.api._

      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += godRoleUser,
          authTokenDao.AuthTokens += godRoleUserAuthToken
        )), Inf)

      val minutesPassed = 20
      TimeServiceMock.injectedTime = TimeServiceMock.injectedTime.plusMinutes(minutesPassed)

      val result = route(app, FakeRequest(POST, "/auth", FakeHeaders().add(jsonContentTypeHeader), request.toString)).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      (contentAsJson(result) \ "token").get.toString must equalTo(s""""${godRoleUserAuthToken.token}"""")

      whenReady(authTokenDao.findToken(godRoleUserAuthToken.token))
      {
        val expectedAuthToken = godRoleUserAuthToken.copy(expiresAt = TimeServiceMock.injectedTime.plusMinutes(godRoleUser.sessionDuration))
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
