package controllers

import model._
import play.api.inject._
import play.api.inject.guice.GuiceApplicationBuilder
import utils.{AuthTokenGenerator, TimeConversion, TimeService}

trait TestSupport extends TimeConversion {

  val jsonContentTypeHeader = ("Content-type", "application/json")

  def tokenHeader(token: String) = ("token", token)

  val firstAuthToken = "819abae5-3bf2-4f1c-9517-badeebafdb3d"
  val secondAuthToken = "198f33f5-74ba-4007-8f28-63206f98b3d1"
  val thirdAuthToken = "dffb9220-87d3-4d31-a935-9a063aa08022"
  val authTokens = List(firstAuthToken, secondAuthToken, thirdAuthToken)
  val authTokenIterator = authTokens.iterator

  val authTokenGenerator = new AuthTokenGenerator {
    def nextAuthToken(): String = authTokenIterator.next()
  }

  val app = new GuiceApplicationBuilder()
    .overrides(bind[AuthTokenGenerator].toInstance(authTokenGenerator))
    .overrides(bind[TimeService].to[TimeServiceMock])
    .build()


  // fake data
  val englishLanguage = Language(Some(1), "english")
  val frenchLanguage = Language(Some(2), "french")
  val godRoleUser = User(Some(2), Some("a"), Some("b"), "niko", "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", ActiveEnum.id, 30, GodRoleEnum.id)
  val godRoleUserAuthToken = AuthToken(authTokenGenerator.nextAuthToken(), TimeServiceMock.injectedTime, TimeServiceMock.injectedTime.plusMinutes(30), None, active = true, 2)

}
