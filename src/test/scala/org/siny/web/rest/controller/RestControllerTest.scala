package org.siny.web.rest.controller

import com.secer.elastic.model.User
import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus
import org.scalatest.FlatSpec
import org.siny.web.response.HttpResponse

/**
 * siny
 * Created by chengpohi on 9/8/15.
 */
class RestControllerTest extends FlatSpec {
  "RestController " should "can inject functions" in {
    val u: User = User("chengpohi", Option("chengpohi@gmail.com"))

    def t(user: User): HttpResponse = {
      HttpResponse(user.name, HttpResponseStatus.ACCEPTED)
    }

    RestController.registerHandler("GET", "TEST_GET", t)
    def getAction[A] = RestController.getActions.getOrElse("TEST_GET", null).asInstanceOf[A => HttpResponse]
    val httpResponse: HttpResponse = getAction(u)

    assert(RestController.getActions.size == 1)
    assert(httpResponse.content == u.name)
  }
}
