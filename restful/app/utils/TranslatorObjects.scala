package utils

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object TranslatorObjects {

  case class Language(id: Int, name: String)
  val English = Language(id = 1, name = "english")
  val French = Language(id = 2, name = "french")
  val Languages = List(English, French)

  case class WordType(id: Int, wordType: String)
  val Adjective = WordType (id = 1, wordType = "adjective")
  val Adverb = WordType (id = 2, wordType = "adverb")
  val Noun = WordType (id = 3, wordType = "noun")
  val Pronoun = WordType (id = 4, wordType = "pronoun")
  val Verb = WordType (id = 5, wordType = "verb")
  val VerbTransitive = WordType (id = 6, wordType = "verb transitive")

  case class Gender(id: Int, gender: String, short: String)
  val Masculine = Gender(id = 1, gender = "masculine", short = "m.")
  val Feminine = Gender(id = 2, gender = "feminine", short = "f.")

  case class Word(word: String, description: Option[String], language: Language, wordType: Option[WordType], gender: Option[Gender])
  case class Translation(word: Word, translation: Word, examples: List[Example])
  case class Example(sentence: String, translation: String)

  implicit val languageWrites: Writes[Language] = (
    (__ \ "id").write[Int] and
    (__ \ "name").write[String]
  )(unlift(Language.unapply))

  implicit val wordTypeWrites: Writes[WordType] = (
    (__ \ "id").write[Int] and
    (__ \ "wordType").write[String]
  )(unlift(WordType.unapply))

  implicit val genderWrites: Writes[Gender] = (
    (__ \ "id").write[Int] and
    (__ \ "gender").write[String] and
    (__ \ "short").write[String]
  )(unlift(Gender.unapply))

  implicit val exampleWrites: Writes[Example] = (
    (__ \ "sentence").write[String] and
    (__ \ "translation").write[String]
  )(unlift(Example.unapply))

  implicit val wordWrites: Writes[Word] = (
    (__ \ "word").write[String] and
    (__ \ "description").writeNullable[String] and
    (__ \ "language").write[Language] and
    (__ \ "wordType").writeNullable[WordType] and
    (__ \ "gender").writeNullable[Gender]
  )(unlift(Word.unapply))

  implicit val translationWrites: Writes[Translation] = (
    (__ \ "word").write[Word] and
    (__ \ "translation").write[Word] and
    (__ \ "examples").write[Seq[Example]]
  )(unlift(Translation.unapply))
}

