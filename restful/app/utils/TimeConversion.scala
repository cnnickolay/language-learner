package utils

import java.sql.Timestamp

import org.joda.time.DateTime

trait TimeConversion {

  implicit def jodaTimeToTimestamp(dateTime: DateTime): Timestamp = new Timestamp(dateTime.getMillis)

  implicit def timestampToJodaTime(timestamp: Timestamp): DateTime = new DateTime(timestamp.getTime)

}
