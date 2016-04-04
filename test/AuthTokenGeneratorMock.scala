import utils.AuthTokenGenerator

class AuthTokenGeneratorMock extends AuthTokenGenerator {
  def nextAuthToken(): String = AuthTokenGeneratorMock.nextToken
}

object AuthTokenGeneratorMock {
  val nextToken = "819abae5-3bf2-4f1c-9517-badeebafdb3d"
}