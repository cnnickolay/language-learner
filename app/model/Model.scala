package model

import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax
import play.api.libs.json.{Json, JsPath, Writes}

object Model {

  case class Media(id: Option[Long], name: String)
  case class Subtitle(id: Option[Long], pos: Int, mediaId: Long)
  case class Word(id: Option[Long], word: String)
  case class WordSubtitle(id: Option[Long], time: BigDecimal, pos: Int, wordId: Long, subtitleId: Long)

  implicit val mediaFormat: Format[Media] = (
    (__ \ "id").formatNullable[Long] and
    (__ \ "name").format[String]
  )(Media.apply, unlift(Media.unapply))

  implicit val subtitleFormat: Format[Subtitle] = (
    (__ \ "id").formatNullable[Long] and
    (__ \ "pos").format[Int] and
    (__ \ "mediaId").format[Long]
  )(Subtitle.apply, unlift(Subtitle.unapply))

  implicit val wordFormat: Format[Word] = (
    (__ \ "id").formatNullable[Long] and
    (__ \ "word").format[String]
  )(Word.apply, unlift(Word.unapply))

  implicit val wordSubtitleFormat: Format[WordSubtitle] = (
    (__ \ "id").formatNullable[Long] and
    (__ \ "time").format[BigDecimal] and
    (__ \ "pos").format[Int] and
    (__ \ "wordId").format[Long] and
    (__ \ "subtitleId").format[Long]
  )(WordSubtitle.apply, unlift(WordSubtitle.unapply))
}
