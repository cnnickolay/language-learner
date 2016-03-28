package utils.actions

import play.api.http.HeaderNames
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

import scala.concurrent.Future

object CORSAction extends ActionBuilder[Request] with HeaderNames {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    val futureRes: Future[Result] = block(request)
    futureRes.map{ res =>
      res.withHeaders(
        ACCESS_CONTROL_ALLOW_ORIGIN -> "http://localhost:3000",
        ACCESS_CONTROL_ALLOW_METHODS -> "*",
        ACCESS_CONTROL_MAX_AGE -> "3600",
        ACCESS_CONTROL_ALLOW_HEADERS ->  s"$ORIGIN, X-Requested-With, $CONTENT_TYPE, $ACCEPT, $AUTHORIZATION, X-Auth-Token",
        ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true")
    }
  }
}
