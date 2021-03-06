package controllers

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils.actions.{CORSAction, UserAction, AuthTokenRefreshAction, ActionsConfiguration}
import utils.{TranslatorObjects, Translator}

class TranslationController @Inject()(val userAction: UserAction,
                                      val authTokenRefreshAction: AuthTokenRefreshAction,
                                      val corsAction: CORSAction) extends Controller with ActionsConfiguration {

  def translate(from: String, to: String, word: String) = authAction {
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
