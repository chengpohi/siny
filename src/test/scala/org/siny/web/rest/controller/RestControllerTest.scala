package org.siny.web.rest.controller

import com.secer.elastic.model.User
import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus
import org.scalatest.FlatSpec
import org.siny.web.response.HttpResponse
import org.json4s._
import org.json4s.native.JsonMethods._

/**
 * siny
 * Created by chengpohi on 9/8/15.
 */
class RestControllerTest extends FlatSpec {
  implicit val formats = DefaultFormats
  val userJSON = """{"name":"chengpohi","email":"chengpohi@gmail.com","password":"123QQQ"} """

  "RestController " should "can inject functions" in {
    val u: User = User("chengpohi", Option("chengpohi@gmail.com"))

    def t(user: User): HttpResponse = {
      println("UserName: " + user.name)
      HttpResponse(user.name, HttpResponseStatus.ACCEPTED)
    }

    RestController.registerHandler("GET", "TEST_GET", t)

    def getAction[A] = RestController.getActions.getOrElse("TEST_GET", null)._1.asInstanceOf[A => HttpResponse]
    val httpResponse: HttpResponse = getAction(u)

    def testWithTypeErasure(): Unit = {
      implicit val mf = RestController.getActions.getOrElse("TEST_GET", null)._2
      val us = parse(userJSON).extract
      val httpResponseJSON: HttpResponse = getAction(us)
      assert(httpResponseJSON.content == u.name)
    }

    assert(RestController.getActions.size == 1)
    assert(httpResponse.content == u.name)
    testWithTypeErasure()
  }

  "RestController " should "can inject functions by tuple parameters" in {
    val u: User = User("chengpohi", Option("chengpohi@gmail.com"))

    def t(user: (User, String)): HttpResponse = {
      println("UserName: " + user._1.name)
      println("String: " + user._2)
      HttpResponse(user._1.name, HttpResponseStatus.ACCEPTED)
    }

    RestController.registerHandler("GET", "TEST_GET", t)

    def getAction[A] = RestController.getActions.getOrElse("TEST_GET", null)._1.asInstanceOf[A => HttpResponse]
    val httpResponse: HttpResponse = getAction((u, "Hello"))

    assert(RestController.getActions.size == 1)
    assert(httpResponse.content == u.name)
  }
}
