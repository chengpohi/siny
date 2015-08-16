package org.siny.web.server.handler

import org.elasticsearch.common.netty.channel._
import org.elasticsearch.common.netty.handler.codec.http.HttpMethod._
import org.elasticsearch.common.netty.handler.codec.http.HttpRequest
import org.siny.web.server.response.ResponseWriter.{writeBuffer, writeFile}
import org.siny.web.server.rest.controller.RestController
import org.siny.web.server.rest.controller.RestController.getActions
import org.siny.web.server.session.HttpSession
import org.slf4j.LoggerFactory

/**
 * Rest Server Handler to deal Http Request
 * Created by chengpohi on 4/18/15.
 */
class RestServerHandler extends SimpleChannelUpstreamHandler {
  lazy val LOG = LoggerFactory.getLogger(getClass.getName)



  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) = {
    val httpRequest = e.getMessage.asInstanceOf[HttpRequest]
    val httpSession = HttpSession(null, httpRequest)

    val uri: String = httpRequest.getUri
    LOG.info("IP: " + e.getRemoteAddress + " VISIT URL: " + uri + " Method:" + httpRequest.getMethod)
    val f: RestController.DealerType = httpRequest.getMethod match {
      case GET => getActions.getOrElse(uri, null)
      case POST => getActions.getOrElse(uri, null)
      case PUT => getActions.getOrElse(uri, null)
      case DELETE => getActions.getOrElse(uri, null)
      case _ => null
    }

    f match {
      case null => writeFile(ctx.getChannel, uri, httpSession)
      case _ => writeBuffer(ctx.getChannel, f(httpSession))
    }
  }

  @throws(classOf[Exception])
  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent): Unit = {
    LOG.warn("Exception: " + e.toString)
  }


  override def channelDisconnected(ctx: ChannelHandlerContext, e: ChannelStateEvent) = {
    super.channelDisconnected(ctx, e)
  }
}
