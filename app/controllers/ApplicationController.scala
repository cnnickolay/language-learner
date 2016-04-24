package controllers

import com.google.inject.Inject
import play.api.mvc.Controller
import utils.actions.{AuthTokenRefreshAction, UserAction, ActionsConfiguration, CORSAction}

class ApplicationController @Inject() (val userAction: UserAction,
                                       val authTokenRefreshAction: AuthTokenRefreshAction,
                                       val corsAction: CORSAction) extends Controller with ActionsConfiguration {

  def options(path: String) = corsAction {
    Ok
  }

}
