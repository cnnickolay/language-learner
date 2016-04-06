package controllers.specs

import controllers.{FakeDataGen, TestSupport, WithLangApplication}
import model.MediaGroup
import org.junit.runner._
import org.scalatest.concurrent.ScalaFutures
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeHeaders, FakeRequest}
import slick.dbio

import scala.concurrent.Await
import scala.concurrent.duration.Duration._

@RunWith(classOf[JUnitRunner])
class MediaGroupControllerSpec extends Specification with TestSupport with ScalaFutures {

  "GET /mediaGroups" should {

    "return all existing media groups" in new WithLangApplication(app) {
      import dbProvider.driver.api._

      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += godRoleUser,
          authTokenDao.AuthTokens += godRoleUserAuthToken,
          mediaGroupDao.MediaGroups ++= Seq(
            MediaGroup(Some(1), "Group 1", Some("description for group 1"), 1),
            MediaGroup(Some(2), "Group 2", None, 2)
          )
        )
      ), Inf)

      val result = route(app, FakeRequest(GET, "/mediaGroups", FakeHeaders().add(tokenHeader(godRoleUserAuthToken.token)), "")).get

      status(result) should equalTo(OK)
      contentAsJson(result) should equalTo(Json.parse(
        """
          |[
          |  {
          |    "id": 1,
          |    "name": "Group 1",
          |    "description": "description for group 1",
          |    "languageId": 1
          |  },
          |  {
          |    "id": 2,
          |    "name": "Group 2",
          |    "languageId": 2
          |  }
          |]
        """.stripMargin))
    }

  }

  "GET /mediaGroups/:id" should {

    "return one media group if it exists" in new WithLangApplication(app) {
      import dbProvider.driver.api._

      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += godRoleUser,
          authTokenDao.AuthTokens += godRoleUserAuthToken,
          mediaGroupDao.MediaGroups ++= Seq(
            MediaGroup(Some(1), "Group 1", Some("description for group 1"), 1)
          )
        )
      ), Inf)

      val result = route(app, FakeRequest(GET, "/mediaGroups/1", FakeHeaders().add(tokenHeader(godRoleUserAuthToken.token)), "")).get

      status(result) should equalTo(OK)
      contentAsJson(result) should equalTo(Json.parse(
        """
          |{
          |  "id": 1,
          |  "name": "Group 1",
          |  "description": "description for group 1",
          |  "languageId": 1
          |}
        """.stripMargin))
    }

    "return error if media group does not exist" in new WithLangApplication(app) {
      import dbProvider.driver.api._

      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += godRoleUser,
          authTokenDao.AuthTokens += godRoleUserAuthToken
        )
      ), Inf)

      val result = route(app, FakeRequest(GET, "/mediaGroups/1", FakeHeaders().add(tokenHeader(godRoleUserAuthToken.token)), "")).get

      status(result) should equalTo(BAD_REQUEST)
      contentAsString(result) must equalTo(s"Media group with id 1 does not exist")
    }
  }

  "POST /mediaGroups" should {

    "create new media group" in new WithLangApplication(app) with FakeDataGen {
      import dbProvider.driver.api._

      val name = generateText(20)
      val description = generateText(100)
      val request =
        s"""
          |{
          |  "name": "$name",
          |  "description": "$description",
          |  "languageId": 1
          |}
        """.stripMargin

      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += godRoleUser,
          authTokenDao.AuthTokens += godRoleUserAuthToken
        )
      ), Inf)

      val result = route(app, FakeRequest(POST, "/mediaGroups", FakeHeaders().add(tokenHeader(godRoleUserAuthToken.token)).add(jsonContentTypeHeader), request)).get
      status(result) should equalTo(OK)
      whenReady(mediaGroupDao.byName(name)) { _ must equalTo(Some(MediaGroup(Some(1), name, Some(description), 1))) }
    }
  }

  "DELETE /mediaGroups/:id" should {

    "delete existing media group" in new WithLangApplication(app) {
      import dbProvider.driver.api._

      Await.result(dbProvider.db.run(
        dbio.DBIO.seq(
          userDao.Users += godRoleUser,
          authTokenDao.AuthTokens += godRoleUserAuthToken,
          mediaGroupDao.MediaGroups ++= Seq(
            MediaGroup(Some(1), "Group 1", Some("description for group 1"), 1)
          )
        )
      ), Inf)

      val result = route(app, FakeRequest(DELETE, "/mediaGroups/1", FakeHeaders().add(tokenHeader(godRoleUserAuthToken.token)), "")).get
      status(result) should equalTo(OK)
      whenReady(mediaGroupDao.byId(1)) { _ must equalTo(None) }
    }
  }

}
