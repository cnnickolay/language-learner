package utils

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object TranslatorObjects {

  case class Language(id: Int, name: String)
  case object English extends Language(id = 1, name = "english")
  case object French extends Language(id = 2, name = "french")

  sealed trait WordType{ val id: Int; val wordType: String }
  case object Adjective extends WordType {val id = 1; val wordType = "adjective"}
  case object Adverb extends WordType {val id = 2; val wordType = "adverb"}
  case object Noun extends WordType {val id = 3; val wordType = "noun"}
  case object Verb extends WordType {val id = 4; val wordType = "verb"}

  sealed trait Gender { val id: Int; val gender: String }
  case object Male extends Gender {val id = 1; val gender = "male"}
  case object Female extends Gender {val id = 2; val gender = "female"}

  case class Word(word: String, description: Option[String], language: Language, wordType: Option[WordType], gender: Option[Gender])
  case class Translation(word: Word, translation: Word, examples: List[Example])
  case class Example(sentence: String, translation: String)

  implicit val languageWrites: Writes[Language] = (
    (__ \ "id").write[Int] and
    (__ \ "name").write[String]
  )(unlift(Language.unapply))
}

