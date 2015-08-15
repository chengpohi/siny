package org.siny.web.server.response

import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus

/**
 * siny
 * Created by chengpohi on 8/15/15.
 */
case class HttpResponse(content: String, status: HttpResponseStatus)
