package model

sealed abstract class LanguageEnum(val id: Int, val name: String)
case object EnglishLanguageEnum extends LanguageEnum(1, "english")
case object FrenchLanguageEnum extends LanguageEnum(2, "french")
case object GermanLanguageEnum extends LanguageEnum(3, "german")

sealed abstract class RoleEnum(val id: Int, val name: String)
case object GodRoleEnum extends RoleEnum(1, "god")
case object AdminRoleEnum extends RoleEnum(2, "admin")
case object TeacherRoleEnum extends RoleEnum(3, "teacher")
case object StudentRoleEnum extends RoleEnum(4, "student")
