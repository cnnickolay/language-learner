package controllers

import org.joda.time.DateTime
import utils.TimeService

class TimeServiceMock extends TimeService {
  def now: DateTime = TimeServiceMock.injectedTime
}

object TimeServiceMock {
  var injectedTime: DateTime = DateTime.now().withYear(2015).withMonthOfYear(1).withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
}
