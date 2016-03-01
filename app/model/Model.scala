package model

import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax
import play.api.libs.json.{Json, JsPath, Writes}

object Model {

  case class Media(id: Option[Long], name: String, mediaUrl: String)
  case class Subtitle(id: Option[Long], pos: Int, offset: BigDecimal, text: String, mediaId: Long)

  implicit val mediaFormat: Format[Media] = (
    (__ \ "id").formatNullable[Long] and
    (__ \ "name").format[String] and
    (__ \ "media_url").format[String]
  )(Media.apply, unlift(Media.unapply))

  implicit val subtitleFormat: Format[Subtitle] = (
    (__ \ "id").formatNullable[Long] and
    (__ \ "pos").format[Int] and
    (__ \ "offset").format[BigDecimal] and
    (__ \ "text").format[String] and
    (__ \ "mediaId").format[Long]
  )(Subtitle.apply, unlift(Subtitle.unapply))

}
