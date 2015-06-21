package org.siny.web.server.handler


import org.elasticsearch.common.netty.channel._
import org.elasticsearch.common.netty.handler.codec.http.HttpRequest
import org.elasticsearch.common.netty.handler.codec.http.HttpMethod._
import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST

import org.siny.web.server.SinyServerListener
import org.slf4j.LoggerFactory

/**
 * Created by chengpohi on 4/18/15.
 */
class HttpResponseSinyServerHandler extends SimpleChannelUpstreamHandler {
  lazy val LOG = LoggerFactory.getLogger(getClass.getName)
  lazy val sinyServerListener = new SinyServerListener

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) = {
    val httpRequest = e.getMessage.asInstanceOf[HttpRequest]
    val uri = httpRequest.getUri

    LOG.info("IP: " + e.getRemoteAddress + " VISIT URL: " + uri + " Method:" + httpRequest.getMethod)

    httpRequest.getMethod match {
      case GET => sinyServerListener.getListener(ctx, uri)
      case PUT => sinyServerListener.getListener(ctx, uri)
      case DELETE => sinyServerListener.deleteListener(ctx, httpRequest)
      case POST => sinyServerListener.postListener(ctx, httpRequest)
    }
  }

  @throws(classOf[Exception])
  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent): Unit = {
    sinyServerListener.writeBuffer(ctx.getChannel, "Exception Has Occurred".getBytes, BAD_REQUEST)
    LOG.warn("Exception: " + e.getCause.getLocalizedMessage)
  }


  override def channelDisconnected(ctx: ChannelHandlerContext, e: ChannelStateEvent) = {
    super.channelDisconnected(ctx, e)
  }
}
