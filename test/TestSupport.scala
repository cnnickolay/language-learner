import java.sql.Timestamp
import java.util.UUID

import model._
import org.joda.time.DateTime
import play.api.inject._
import play.api.inject.guice.GuiceApplicationBuilder
import utils.AuthTokenGenerator

trait TestSupport {

  val jsonContentTypeHeader = ("Content-type", "application/json")
  val englishLanguage = Language(Some(1), "english")
  val frenchLanguage = Language(Some(2), "french")
  val godRoleUser = User(Some(2), Some("a"), Some("b"), "niko", "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", ActiveEnum.id, 30, GodRoleEnum.id)
  val godRoleUserAuthToken = AuthToken(UUID.randomUUID().toString, new Timestamp(DateTime.now().getMillis), new Timestamp(DateTime.now().plusMinutes(30).getMillis), None, active = true, 2)
  def tokenHeader(token: String) = ("token", token)

  def app = new GuiceApplicationBuilder()
    .overrides(bind[AuthTokenGenerator].to[AuthTokenGeneratorMock])
    .build()

}
