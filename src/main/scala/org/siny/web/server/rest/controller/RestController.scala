package org.siny.web.server.rest.controller

import org.siny.web.server.response.HttpResponse
import org.siny.web.server.session.HttpSession

/**
 * RestController
 * Created by chengpohi on 8/14/15.
 */
class RestController {
  val GET = "GET"
  val POST = "POST"
  val PUT = "PUT"
  val DELETE = "DELETE"

  type DealerType = HttpSession => HttpResponse

  var getActions = scala.collection.mutable.Map[String, DealerType]()
  var postActions = scala.collection.mutable.Map[String, DealerType]()
  var putActions = scala.collection.mutable.Map[String, DealerType]()
  var deleteActions = scala.collection.mutable.Map[String, DealerType]()

  def registerHandler(method: String, path: String, f: HttpSession => HttpResponse): Unit = {
    method match {
      case GET => getActions += path -> f _
      case POST => postActions += path -> f _
      case PUT => putActions += path -> f _
      case DELETE => deleteActions += path -> f _
      case _ =>
    }
  }
}
