package org.siny.controller

import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus
import org.siny.web.server.response.HttpResponse
import org.siny.web.server.rest.controller.RestAction
import org.siny.web.server.session.HttpSession

/**
 * User Controller
 * Created by chengpohi on 8/1/15.
 */

object UserController extends RestAction {
  def createUser(httpSession: HttpSession): HttpResponse = {
    null
  }

  def userLogin(httpSession: HttpSession): HttpResponse = {
    null
  }

  def userInfo(httpSession: HttpSession): HttpResponse = {
    HttpResponse("Hello Chengpohi", HttpResponseStatus.ACCEPTED)
  }
}
