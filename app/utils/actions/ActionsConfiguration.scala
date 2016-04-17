package utils.actions

import model.AdminRoleEnum

trait ActionsConfiguration {
  val userAction: UserAction
  val authTokenRefreshAction: AuthTokenRefreshAction

  val adminRoleFilter: AuthorizationFilter = new AuthorizationFilter(AdminRoleEnum)

  val action = CORSAction // default action
  val userActionWithCORS = CORSAction andThen userAction andThen authTokenRefreshAction
  val authAction = CORSAction andThen userAction andThen AuthenticationAction andThen authTokenRefreshAction
  val adminAction  = CORSAction andThen userAction andThen AuthenticationAction andThen adminRoleFilter andThen authTokenRefreshAction
}
