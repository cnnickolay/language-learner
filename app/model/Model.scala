package model

import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax
import play.api.libs.json._

object Model {

  case class Media(id: Option[Long], name: String, mediaUrl: String)
  case class Subtitle(id: Option[Long], offset: Option[BigDecimal], text: String, mediaId: Long)
  case class SubtitlesSrtRaw(mediaId: Long, srt: String)

  implicit val mediaFormat: Format[Media] = (
    (__ \ "id").formatNullable[Long] and
    (__ \ "name").format[String] and
    (__ \ "mediaUrl").format[String]
  )(Media.apply, unlift(Media.unapply))

  implicit val subtitleFormat: Format[Subtitle] = (
    (__ \ "id").formatNullable[Long] and
    (__ \ "offset").formatNullable[BigDecimal] and
    (__ \ "text").format[String] and
    (__ \ "mediaId").format[Long]
  )(Subtitle.apply, unlift(Subtitle.unapply))

  implicit val subtitleSrtRawReads: Reads[SubtitlesSrtRaw] = (
    (__ \ "mediaId").read[Long] and
    (__ \ "srt").read[String]
  )(SubtitlesSrtRaw.apply _)

}
