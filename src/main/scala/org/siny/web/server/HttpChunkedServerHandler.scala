package org.siny.web.server

import org.jboss.netty.channel.{ExceptionEvent, ChannelHandlerContext, MessageEvent, SimpleChannelUpstreamHandler}

/**
 * Created by xiachen on 4/18/15.
 */
class HttpChunkedServerHandler() extends SimpleChannelUpstreamHandler {
  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) = {
    println("hello world")
  }

  @throws(classOf[Exception])
  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent): Unit = {
    println("Exception")
  }
}
