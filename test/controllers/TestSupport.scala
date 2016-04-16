package controllers

import model._
import org.joda.time.DateTime
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
  var authTokenIterator = authTokens.iterator

  val authTokenGenerator = new AuthTokenGenerator {
    def nextAuthToken: String = authTokenIterator.next()
  }

  /**
    * simulate time pass
    */
  def minutesPassed(minutes: Int) = {
    presentTime = presentTime.plusMinutes(minutes)
  }

  val defaultNow = DateTime.now().withYear(2015).withMonthOfYear(1).withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
  var presentTime = defaultNow
  val timeService = new TimeService {
    def now = presentTime
  }

  def app = {
    this.authTokenIterator = authTokens.iterator
    presentTime = defaultNow

    new GuiceApplicationBuilder()
      .overrides(bind[AuthTokenGenerator].toInstance(authTokenGenerator))
      .overrides(bind[TimeService].toInstance(timeService))
      .build()
  }

  // fake data
  var englishLanguage = Language(Some(1), "english")
  var frenchLanguage = Language(Some(2), "french")
  var godRoleUser = User(Some(2), Some("a"), Some("b"), "niko", "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", ActiveEnum.id, 30, GodRoleEnum.id)
  def defaultAuthToken = AuthToken(authTokenGenerator.nextAuthToken, presentTime, presentTime.plusMinutes(30), None, active = true, 2)

  val SHA256_123 = "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3"
}
