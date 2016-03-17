package model

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object Model {

  case class MediaGroup(id: Option[Long], name: String, description: Option[String], languageId: Int)
  case class Media(id: Option[Long], name: String, description: Option[String], mediaUrl: String, mediaGroupId: Option[Long])
  case class Subtitle(id: Option[Long], offset: Option[BigDecimal], text: String, mediaId: Option[Long])
  case class Language(id: Option[Int], name: String)

  case class SubtitlesSrtRaw(mediaId: Option[Long], srt: String)

  implicit val mediaGroupFormat: Format[MediaGroup] = (
    (__ \ "id").formatNullable[Long] and
    (__ \ "name").format[String] and
    (__ \ "description").formatNullable[String] and
    (__ \ "languageId").format[Int]
  )(MediaGroup.apply, unlift(MediaGroup.unapply))

  implicit val mediaFormat: Format[Media] = (
    (__ \ "id").formatNullable[Long] and
    (__ \ "name").format[String] and
    (__ \ "description").formatNullable[String] and
    (__ \ "mediaUrl").format[String] and
    (__ \ "mediaGroupId").formatNullable[Long]
  )(Media.apply, unlift(Media.unapply))

  implicit val subtitleFormat: Format[Subtitle] = (
    (__ \ "id").formatNullable[Long] and
    (__ \ "offset").formatNullable[BigDecimal] and
    (__ \ "text").format[String] and
    (__ \ "mediaId").formatNullable[Long]
  )(Subtitle.apply, unlift(Subtitle.unapply))

  implicit val languageFormat: Format[Language] = (
    (__ \ "id").formatNullable[Int] and
    (__ \ "name").format[String]
  )(Language.apply, unlift(Language.unapply))

  implicit val subtitleSrtRawReads: Reads[SubtitlesSrtRaw] = (
    (__ \ "mediaId").readNullable[Long] and
    (__ \ "srt").read[String]
  )(SubtitlesSrtRaw.apply _)

}
