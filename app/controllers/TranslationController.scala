package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils.{TranslatorObjects, Translator}

class TranslationController extends Controller {

  def translate(from: String, to: String, word: String) = Action {
    println(word)
    val result = for {
      fromLanguage <- TranslatorObjects.Languages.find(_.name == from)
      toLanguage <- TranslatorObjects.Languages.find(_.name == to)
    } yield {
      val translated = Translator.translate(fromLanguage, toLanguage, word)
      Ok(Json.toJson(translated))
    }
    result.getOrElse(BadRequest("Unable to fulfil request"))
  }

}
