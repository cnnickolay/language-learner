package utils

import org.scalatest.FlatSpec
import play.api.libs.json.Json
import utils.TranslatorObjects.{English, French}

class Translator$Test extends FlatSpec {

  ignore should "extract translation" in {
    val translated = Translator.translate(French, English, "chaque")
    val json = Json.toJson(translated)
    println(json)
  }

}
