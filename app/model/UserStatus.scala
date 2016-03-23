package model

sealed abstract class UserStatus(id: Integer, status: String)
case object Active extends UserStatus(1, "active")
case object Cancelled extends UserStatus(2, "cancelled")
case object Suspended extends UserStatus(3, "suspended")