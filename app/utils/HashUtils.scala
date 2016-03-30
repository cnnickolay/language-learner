package utils

import java.nio.charset.StandardCharsets

import com.google.common.hash.Hashing

object HashUtils {

  def calculateSha256(str: String) = {
    Hashing.sha256().hashString(str, StandardCharsets.UTF_8).toString
  }

}
