package org.siny.controller

import com.secer.elastic.controller.UserController
import com.secer.elastic.model.User
import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus
import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus.{BAD_REQUEST, OK}
import org.elasticsearch.common.netty.util.CharsetUtil
import org.siny.web.server.cache.LoginUserCache
import org.siny.web.server.response.HttpResponse
import org.siny.web.server.rest.controller.RestAction
import org.siny.web.server.session.HttpSession
import org.json4s.jackson.JsonMethods._

/**
 * User Controller
 * Created by chengpohi on 8/1/15.
 */

object UserAction extends RestAction {
  val LOGIN_INPUT_TIP: String = "Please input email with password!"
  val LOGIN_ERROR_TIP: String = "Wrong Email or Password."
  val LOGIN_SUCCESSFUL_TIP: String = "Login Successfully"

  def registerUser(httpSession: HttpSession): HttpResponse = {
    val rawUser = httpSession.httpRequest.getContent.toString(CharsetUtil.UTF_8)

    val user = parse(rawUser).extract[User]

    val resultId = UserController.createUserInfo(user)
    HttpResponse(resultId, OK)
  }

  def userLogin(httpSession: HttpSession): HttpResponse = {
    val rawUser = httpSession.httpRequest.getContent.toString(CharsetUtil.UTF_8).split("&")

    def validateUser(): HttpResponse = {
      val email = rawUser(0).split("=")(1)
      val password = rawUser(1).split("=")(1)

      val user = User("", Option(email), Option(password))
      UserController.validateUserLogin(user) match {
        case u: User =>
          LoginUserCache.LOGINED_USER += u.cookie.get -> u
          HttpResponse(LOGIN_SUCCESSFUL_TIP, OK, Option(u.cookie.get))
        case null =>
          HttpResponse(LOGIN_ERROR_TIP, BAD_REQUEST)
      }
    }

    val httpResponse = rawUser(0).endsWith("=") || rawUser(1).endsWith("=") match {
      case true =>
        HttpResponse(LOGIN_INPUT_TIP, OK)
      case false => validateUser()
    }
    httpResponse
  }

  def userInfo(httpSession: HttpSession): HttpResponse = {
    HttpResponse("Hello Chengpohi", HttpResponseStatus.ACCEPTED)
  }
}
