package utils

import org.scalatest.FunSuite

class HashUtils$Test extends FunSuite {

  test("sha256 is calculated properly") {
    val sha256 = HashUtils.calculateSha256("hello_there")
    assert(sha256 == "299d2e40d6b7026b6029b8ff4cff0ad0fbfe14b20d704a609a2631cada32fbc1")
  }

}
