package utils.actions

import model.{GodRoleEnum, AdminRoleEnum}

trait ActionsConfiguration {
  val userAction: UserAction
  val authTokenRefreshAction: AuthTokenRefreshAction
  val corsAction: CORSAction

  val action = corsAction // default action
  val userActionWithCORS = corsAction andThen userAction andThen authTokenRefreshAction
  val authAction = corsAction andThen userAction andThen AuthenticationAction andThen authTokenRefreshAction
  val adminAction  = corsAction andThen userAction andThen AuthenticationAction andThen AuthorizationFilter(AdminRoleEnum) andThen authTokenRefreshAction
  val adminAndGodAction  = corsAction andThen userAction andThen AuthenticationAction andThen AuthorizationFilter(AdminRoleEnum, GodRoleEnum) andThen authTokenRefreshAction
}
