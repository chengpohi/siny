package org.siny.web.server.handler

import org.elasticsearch.common.netty.channel._
import org.elasticsearch.common.netty.handler.codec.http.HttpMethod._
import org.elasticsearch.common.netty.handler.codec.http.HttpRequest
import org.siny.web.server.response.ResponseWriter
import org.siny.web.server.rest.controller.RestController
import org.siny.web.server.session.HttpSession
import org.slf4j.LoggerFactory

/**
 * Created by chengpohi on 4/18/15.
 */
class RestServerHandler extends SimpleChannelUpstreamHandler {
  lazy val LOG = LoggerFactory.getLogger(getClass.getName)

  val restController = new RestController()
  val responseWriter = new ResponseWriter()

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) = {
    val httpRequest = e.getMessage.asInstanceOf[HttpRequest]
    val httpSession = HttpSession(null, httpRequest)

    LOG.info("IP: " + e.getRemoteAddress + " VISIT URL: " + httpRequest.getUri + " Method:" + httpRequest.getMethod)
    val f: restController.DealerType = httpRequest.getMethod match {
      case GET => restController.getActions.getOrElse(httpRequest.getUri, null)
      case POST => restController.getActions.getOrElse(httpRequest.getUri, null)
      case PUT => restController.getActions.getOrElse(httpRequest.getUri, null)
      case DELETE => restController.getActions.getOrElse(httpRequest.getUri, null)
      case _ => null
    }

    responseWriter.writeBuffer(ctx.getChannel, f(httpSession))
  }

  @throws(classOf[Exception])
  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent): Unit = {
    LOG.warn("Exception: " + e.toString)
  }


  override def channelDisconnected(ctx: ChannelHandlerContext, e: ChannelStateEvent) = {
    super.channelDisconnected(ctx, e)
  }
}
