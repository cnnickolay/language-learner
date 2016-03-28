package utils.actions

trait ActionsConfiguration {
  val userAction: UserAction
  val authTokenRefreshAction: AuthTokenRefreshAction

  val action = CORSAction // default action
  val userActionWithCORS = CORSAction andThen userAction andThen authTokenRefreshAction
  val authActionWithCORS = CORSAction andThen userAction andThen AuthenticationAction andThen authTokenRefreshAction
}
