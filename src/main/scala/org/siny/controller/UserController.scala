package org.siny.controller

import org.siny.web.server.response.HttpResponse
import org.siny.web.server.rest.controller.{RestAction, RestController}
import org.siny.web.server.session.HttpSession

/**
 * User Controller
 * Created by chengpohi on 8/1/15.
 */

class UserController(httpSession: HttpSession, controller: RestController) extends RestAction{

  def apply(): Unit = {
    controller.registerHandler("POST", "/register.html", this.createUser)
    controller.registerHandler("POST", "/login.html", this.userLogin)
  }

  def createUser(httpSession: HttpSession): HttpResponse = {
    null
  }

  def userLogin(httpSession: HttpSession): HttpResponse = {
    null
  }
}
