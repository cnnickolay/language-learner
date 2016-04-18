package object model {
  sealed abstract class LanguageEnum(val id: Int, val name: String) {
    val Languages = EnglishLanguageEnum :: FrenchLanguageEnum :: GermanLanguageEnum :: Nil
  }

  case object EnglishLanguageEnum extends LanguageEnum(1, "english")
  case object FrenchLanguageEnum extends LanguageEnum(2, "french")
  case object GermanLanguageEnum extends LanguageEnum(3, "german")

  val Languages: Seq[LanguageEnum] = EnglishLanguageEnum :: FrenchLanguageEnum :: GermanLanguageEnum :: Nil
  def languageByName(name: String) = Languages.find(_.name == name)
}
