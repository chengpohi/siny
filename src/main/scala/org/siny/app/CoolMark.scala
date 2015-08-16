package org.siny.app

import org.siny.web.server.app.Siny

import org.siny.controller.UserAction.{registerUser, userInfo, userLogin}
import org.siny.controller.{BookMarkController, TabController}
import org.siny.web.server.rest.controller.RestController.registerHandler

/**
 * siny
 * Created by chengpohi on 8/16/15.
 */
object CoolMark extends Siny {
  def main(args: Array[String]): Unit = {
    this.initialize()
  }

  override def registerPath(): Unit = {
    registerHandler("POST", "/register.html", registerUser)
    registerHandler("POST", "/login.html", userLogin)
    registerHandler("GET", "/user.html", userInfo)

    registerHandler("GET", "/bookmark", BookMarkController.getBookMarks)
    registerHandler("POST", "/bookmark", BookMarkController.postBookMark)
    registerHandler("DELETE", "/bookmark", BookMarkController.deleteBookMark)

    registerHandler("POST", "/tab", TabController.postBookMark)
  }
}
