package org.siny.web.server.handler

import java.io.{File, RandomAccessFile}

import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.channel._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.jboss.netty.handler.codec.http.{DefaultHttpResponse, HttpHeaders, HttpRequest, HttpResponseStatus}
import org.jboss.netty.handler.stream.ChunkedFile
import org.siny.web.server.file.FileUtils._


/**
 * Created by xiachen on 4/18/15.
 */
class HttpResponseServerHandler extends SimpleChannelUpstreamHandler {
  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) = {
    val httpRequest = e.getMessage.asInstanceOf[HttpRequest]
    sendPrepare(ctx, httpRequest.getUri)
  }

  @throws(classOf[Exception])
  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent): Unit = {
    println("Exception" + e.getCause)
  }


  override def channelDisconnected(ctx: ChannelHandlerContext, e: ChannelStateEvent) = {
    super.channelDisconnected(ctx, e)
  }

  def sendPrepare(ctx: ChannelHandlerContext, uri: String) {
    val file = getFile(uri)
    file.exists match {
      case true => writeFile(ctx.getChannel, file, OK)
      case false => writeBuffer(ctx.getChannel, uri + ":" + " 404 Not Found", NOT_FOUND)
    }
  }

  def writeFile(channel: Channel, file: File, status: HttpResponseStatus): Unit = {
    val indexFile = new RandomAccessFile(file, "r")

    val response = new DefaultHttpResponse(HTTP_1_1, status)
    response.headers.add(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8")
    response.headers.add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
    response.headers.add(HttpHeaders.Names.CONTENT_LENGTH, indexFile.length())

    channel.write(response)

    channel.write(new ChunkedFile(indexFile, 0, indexFile.length(), 8192))
  }

  def writeBuffer(channel: Channel, data: String, status: HttpResponseStatus): Unit = {
    val response = new DefaultHttpResponse(HTTP_1_1, status)
    response.setContent(ChannelBuffers.wrappedBuffer(data.getBytes))
    response.headers.add(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8")
    response.headers.add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
    response.headers.add(HttpHeaders.Names.CONTENT_LENGTH, response.getContent.readableBytes)

    channel.write(response)
  }
}
