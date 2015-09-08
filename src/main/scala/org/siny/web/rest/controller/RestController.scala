package org.siny.web.rest.controller

import org.siny.web.response.HttpResponse

/**
 * RestController to register controller http request
 * Created by chengpohi on 8/14/15.
 */
object RestController {
  val GET = "GET"
  val POST = "POST"
  val PUT = "PUT"
  val DELETE = "DELETE"

  type DealerType = Object => HttpResponse

  var getActions = scala.collection.mutable.Map[String, DealerType]()
  var postActions = scala.collection.mutable.Map[String, DealerType]()
  var putActions = scala.collection.mutable.Map[String, DealerType]()
  var deleteActions = scala.collection.mutable.Map[String, DealerType]()

  def registerHandler[A](method: String, path: String, f: A => HttpResponse): Unit = {
    method match {
      case GET => getActions += path -> f.asInstanceOf[DealerType]
      case PUT => putActions += path -> f.asInstanceOf[DealerType]
      case DELETE => deleteActions += path -> f.asInstanceOf[DealerType]
      case POST => postActions += path -> f.asInstanceOf[DealerType]
      case _ =>
    }
  }
}
