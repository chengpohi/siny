package org.siny.web.server.handler


import org.jboss.netty.channel._
import org.jboss.netty.handler.codec.http.HttpMethod._
import org.jboss.netty.handler.codec.http._

import org.json4s._
import org.siny.web.server.SinyServerListener
import org.slf4j.LoggerFactory



/**
 * Created by chengpohi on 4/18/15.
 */
class HttpResponseSinyServerHandler extends SimpleChannelUpstreamHandler {
  lazy val LOG = LoggerFactory.getLogger(getClass.getName)
  lazy val sinyServerListener = new SinyServerListener

  implicit val formats = DefaultFormats

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) = {
    val httpRequest = e.getMessage.asInstanceOf[HttpRequest]
    val uri = httpRequest.getUri
    LOG.info("VISIT URL: " + uri + " Method:" + httpRequest.getMethod)

    httpRequest.getMethod match {
      case GET => sinyServerListener.getListener(ctx, uri)
      case PUT => sinyServerListener.getListener(ctx, uri)
      case DELETE => sinyServerListener.deleteListener(ctx, httpRequest)
      case POST => sinyServerListener.postListener(ctx, httpRequest)
    }
  }



}
