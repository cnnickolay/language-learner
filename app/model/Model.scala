package model

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object Model {

  case class Media(id: Option[Long], name: String, mediaUrl: String, languageId: Int)
  case class Subtitle(id: Option[Long], offset: Option[BigDecimal], text: String, mediaId: Long)
  case class SubtitlesSrtRaw(mediaId: Long, srt: String)
  case class Language(id: Option[Int], name: String)

  implicit val mediaFormat: Format[Media] = (
    (__ \ "id").formatNullable[Long] and
    (__ \ "name").format[String] and
    (__ \ "mediaUrl").format[String] and
    (__ \ "languageId").format[Int]
  )(Media.apply, unlift(Media.unapply))

  implicit val subtitleFormat: Format[Subtitle] = (
    (__ \ "id").formatNullable[Long] and
    (__ \ "offset").formatNullable[BigDecimal] and
    (__ \ "text").format[String] and
    (__ \ "mediaId").format[Long]
  )(Subtitle.apply, unlift(Subtitle.unapply))

  implicit val languageFormat: Format[Language] = (
    (__ \ "id").formatNullable[Int] and
    (__ \ "name").format[String]
  )(Language.apply, unlift(Language.unapply))

  implicit val subtitleSrtRawReads: Reads[SubtitlesSrtRaw] = (
    (__ \ "mediaId").read[Long] and
    (__ \ "srt").read[String]
  )(SubtitlesSrtRaw.apply _)

}
