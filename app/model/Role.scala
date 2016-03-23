package model

sealed abstract class Role(name: String)
case object GodRole extends Role("god")
case object AdminRole extends Role("admin")
case object TeacherRole extends Role("teacher")
case object StudentRole extends Role("student")