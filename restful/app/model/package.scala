package object model {
  val Languages: Seq[LanguageEnum] = EnglishLanguageEnum :: FrenchLanguageEnum :: GermanLanguageEnum :: Nil
  def languageByName(name: String) = Languages.find(_.name == name)

  val Roles: Seq[RoleEnum] = GodRoleEnum :: AdminRoleEnum :: TeacherRoleEnum :: StudentRoleEnum :: Nil
  def roleByName(name: String) = Roles.find(_.name == name)
  def roleById(id: Int) = Roles.find(_.id == id)
}
