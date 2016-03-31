package controllers

import play.api.mvc.Controller
import utils.actions.CORSAction

class ApplicationController extends Controller {

  def options(path: String) = CORSAction {
    Ok
  }

}
