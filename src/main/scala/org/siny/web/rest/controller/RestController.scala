package org.siny.web.rest.controller

import org.siny.web.response.HttpResponse
import org.siny.web.session.HttpSession

/**
 * RestController to register controller http request
 * Created by chengpohi on 8/14/15.
 */
object RestController {
  val GET = "GET"
  val POST = "POST"
  val PUT = "PUT"
  val DELETE = "DELETE"

  var getActions = scala.collection.mutable.Map[String, (Any, Manifest[_])]()
  var postActions = scala.collection.mutable.Map[String, (Any, Manifest[_])]()
  var putActions = scala.collection.mutable.Map[String, (Any, Manifest[_])]()
  var deleteActions = scala.collection.mutable.Map[String, (Any, Manifest[_])]()

  def registerHandlerWithHttpSession[A](method: String, path: String, f: (HttpSession, A) => HttpResponse)(implicit mf: Manifest[A]): Unit = {
    method match {
      case GET => getActions += path ->(f, mf)
      case PUT => putActions += path ->(f, mf)
      case DELETE => deleteActions += path ->(f, mf)
      case POST => postActions += path ->(f, mf)
      case _ =>
    }
  }

  def registerHandler[A](method: String, path: String, f: A => HttpResponse)(implicit mf: Manifest[A]): Unit = {
    method match {
      case GET => getActions += path ->(f, mf)
      case PUT => putActions += path ->(f, mf)
      case DELETE => deleteActions += path ->(f, mf)
      case POST => postActions += path ->(f, mf)
      case _ =>
    }
  }
}
