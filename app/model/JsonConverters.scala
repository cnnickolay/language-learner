package model

import java.sql.Timestamp

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object JsonConverters {

  case class UserAuth(login: String, password: String)

  implicit val userAuthReads: Reads[UserAuth] = (
    (__ \ "login").read[String] and
    (__ \ "password").read[String]
  )(UserAuth.apply _)

  implicit val userReads: Reads[User] = (
    (__ \ "id").readNullable[Long] and
    (__ \ "name").readNullable[String] and
    (__ \ "lastname").readNullable[String] and
    (__ \ "login").read[String] and
    (__ \ "passwordHash").read[String] and
    (__ \ "statusId").read[Int] and
    (__ \ "sessionDuration").read[Int] and
    (__ \ "roleId").read[Int]
  )(User.apply _)

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

  implicit val authTokenWrites: Writes[AuthToken] = (
    (__ \ "token").write[String] and
    (__ \ "createdAt").write[Timestamp] and
    (__ \ "expiresAt").write[Timestamp] and
    (__ \ "expiredAt").writeNullable[Timestamp] and
    (__ \ "active").write[Boolean] and
    (__ \ "userId").write[Long]
  )(unlift(AuthToken.unapply))

}
