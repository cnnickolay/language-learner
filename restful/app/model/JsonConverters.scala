package model

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object JsonConverters {

  implicit val languageFormat: Format[Language] = (
    (__ \ "id").format[Int] and
    (__ \ "name").format[String]
  )(Language.apply, unlift(Language.unapply))

}
