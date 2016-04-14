package controllers.specs

import controllers.{TestSupport, WithLangApplication}
import org.junit.runner._
import org.scalatest.concurrent.ScalaFutures
import org.specs2.mutable._
import org.specs2.runner._

@RunWith(classOf[JUnitRunner])
class UserControllerSpec extends Specification with TestSupport with ScalaFutures {

  "GET /users" should {

    "teacher can get a list of his students" in new WithLangApplication(app) {
    }

  }

}
