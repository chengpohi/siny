package org.siny.controller

import com.secer.elastic.model.Tab
import org.elasticsearch.common.netty.util.CharsetUtil
import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus.OK
import com.secer.elastic.controller.ElasticController._
import org.siny.web.server.response.HttpResponse
import org.siny.web.server.rest.controller.RestAction
import org.siny.web.server.session.HttpSession
import org.json4s.jackson.JsonMethods._

/**
 * siny
 * Created by chengpohi on 8/16/15.
 */
object TabController extends RestAction{
  def postBookMark(httpSession: HttpSession): HttpResponse = {
    val rawTab = httpSession.httpRequest.getContent.toString(CharsetUtil.UTF_8)

    val tab = parse(rawTab).extract[Tab]
    val resultId = createTab(httpSession.user, tab)
    HttpResponse(resultId, OK)
  }
}
