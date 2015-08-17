package org.siny.web.response

import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus

/**
 * siny
 * Created by chengpohi on 8/15/15.
 */
case class HttpResponse(content: String, status: HttpResponseStatus, cookie: Option[String] = None)
