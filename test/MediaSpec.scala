import org.scalatest.Ignore
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import play.libs.Json

@RunWith(classOf[JUnitRunner])
class MediaSpec extends Specification {

  "Media" should {

    "get all medias" in new WithApplication {
      val result = route(FakeRequest(GET, "/medias")).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val output = contentAsString(result)
      println(output)
    }

    "insert new media item" in new WithApplication() {
      val request = Json.parse(
        """
          |{
          |  "name": "test2",
          |  "media_url": "http://some_url"
          |}
        """.stripMargin)
      val result = route(FakeRequest(Helpers.POST, "/medias", FakeHeaders().add(("Content-type", "application/json")), request.toString)).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "text/plain")
    }

    "add new media" in new WithApplication() {
      val result = route(FakeRequest(Helpers.GET, "/medias/1")).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")

      val actualJson = Json.parse(contentAsString(result))
      val expectedJson = Json.parse(
        """
          |{
          |  "id": 1,
          |  "name": "Dialogue 1",
          |  "media_url": "https://www.dropbox.com/s/iw9pbrysoe56hf0/Grammaire_en_dialogues_No18.mp3?raw=1"
          |}
        """.stripMargin)
      println(contentAsString(result))
//      Thread.sleep(1000)
    }

  }
}
