import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import play.libs.Json

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "get all medias" in new WithApplication {
      val result = route(FakeRequest(GET, "/medias")).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val output = contentAsString(result)
    }

    "add new media" in new WithApplication {
      val body = """ {"name": "New Media"} """

      println(Json.parse(body))
      val result = route(FakeRequest(Helpers.POST, "/medias", FakeHeaders().add(("Content-type", "application/json")), body)).get


      status(result) must equalTo(OK)
//      contentType(result) must beSome.which(_ == "application/json")
//      val output = contentAsString(result)
    }

  }
}
