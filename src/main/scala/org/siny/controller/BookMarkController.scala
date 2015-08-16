package org.siny.controller

import com.secer.elastic.controller.ElasticController._
import com.secer.elastic.model.BookMark
import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus.OK
import org.elasticsearch.common.netty.util.CharsetUtil
import org.json4s.jackson.JsonMethods._
import org.siny.web.server.response.HttpResponse
import org.siny.web.server.rest.controller.RestAction
import org.siny.web.server.session.HttpSession

/**
 * siny
 * Created by chengpohi on 8/16/15.
 */
object BookMarkController extends RestAction{
  def getBookMarks(httpSession: HttpSession): HttpResponse = {
    HttpResponse(getBookMarksWithJson(httpSession.user), OK)
  }

  def postBookMark(httpSession: HttpSession): HttpResponse = {
    val rawBookMark = httpSession.httpRequest.getContent.toString(CharsetUtil.UTF_8)
    val bookMark = parse(rawBookMark).extract[BookMark]

    createBookMark(httpSession.user, bookMark)
    HttpResponse("Create Success", OK)
  }

  def deleteBookMark(httpSession: HttpSession): HttpResponse = {
    val uri = httpSession.httpRequest.getUri
    val bookMarkID = uri.split("/")(2)
    deleteBookMarkById(bookMarkID, httpSession.user)
    HttpResponse("Delete BookMark Ok, Id: " + bookMarkID, OK)
  }
}
