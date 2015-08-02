package org.siny.controller

import com.secer.elastic.model.User
import org.siny.web.server.annotation.{Component, RequestMapping}
import org.siny.web.server.session.HttpSession

/**
 * User Controller
 * Created by chengpohi on 8/1/15.
 */

@Component
class UserController {
  @RequestMapping(path = "/register.html", method = "POST")
  def createUser(user: User, httpSession: HttpSession): Unit = {
  }

  @RequestMapping(path = "/login.html", method = "POST")
  def userLogin(user: User, httpSession: HttpSession): Unit = {
  }
}
