package utils

import java.util.UUID

import com.google.inject.ImplementedBy

@ImplementedBy(classOf[DefaultAuthTokenGenerator])
trait AuthTokenGenerator {
  def nextAuthToken(): String
}

class DefaultAuthTokenGenerator extends AuthTokenGenerator {
  override def nextAuthToken() = UUID.randomUUID().toString
}