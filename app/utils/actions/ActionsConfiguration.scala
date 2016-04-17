package utils.actions

import model.{GodRoleEnum, AdminRoleEnum}

trait ActionsConfiguration {
  val userAction: UserAction
  val authTokenRefreshAction: AuthTokenRefreshAction

  val action = CORSAction // default action
  val userActionWithCORS = CORSAction andThen userAction andThen authTokenRefreshAction
  val authAction = CORSAction andThen userAction andThen AuthenticationAction andThen authTokenRefreshAction
  val adminAction  = CORSAction andThen userAction andThen AuthenticationAction andThen AuthorizationFilter(AdminRoleEnum) andThen authTokenRefreshAction
  val adminAndGodAction  = CORSAction andThen userAction andThen AuthenticationAction andThen AuthorizationFilter(AdminRoleEnum, GodRoleEnum) andThen authTokenRefreshAction
}
