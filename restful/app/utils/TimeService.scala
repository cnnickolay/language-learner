package utils

import com.google.inject.ImplementedBy
import org.joda.time.DateTime

@ImplementedBy(classOf[DefaultTimeService])
trait TimeService {
  def now: DateTime
}

class DefaultTimeService extends TimeService {
  def now: DateTime = DateTime.now()
}