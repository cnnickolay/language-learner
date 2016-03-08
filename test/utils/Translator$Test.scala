package utils

import org.scalatest.FlatSpec
import play.api.libs.json.Json
import utils.TranslatorObjects.{English, French}

/**
  * Created by Nikolay Cherkezishvili on 07/03/2016
  */
class Translator$Test extends FlatSpec {

  it should "extract translation" in {
    val translated = Translator.translate(French, English, "adorer")
    val json = Json.toJson(translated)
    println(json)
  }

}
