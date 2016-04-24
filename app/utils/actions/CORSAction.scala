package utils.actions

import com.google.inject.Inject
import play.api.Logger
import play.api.http.HeaderNames
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

import scala.concurrent.Future

class CORSAction @Inject()(configuration: play.api.Configuration) extends ActionBuilder[Request] with HeaderNames {
  val l = Logger("utils.actions")

  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    val futureRes: Future[Result] = block(request)
    futureRes.map { res =>
      res.withHeaders(
        ACCESS_CONTROL_ALLOW_ORIGIN -> s"${configuration.getString("cors.allowed.origin").getOrElse("http://localhost")}",
        ACCESS_CONTROL_ALLOW_METHODS -> "POST, GET, DELETE, PUT, OPTIONS",
        ACCESS_CONTROL_MAX_AGE -> "3600",
        ACCESS_CONTROL_ALLOW_HEADERS -> s"$ORIGIN, X-Requested-With, $CONTENT_TYPE, $ACCEPT, $AUTHORIZATION, X-Auth-Token, token")
    }
  }
}
