package org.siny.web.server.handler

import java.io.{File, RandomAccessFile}
import java.net.URL

import org.elasticsearch.common.netty.util.CharsetUtil
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.channel._
import org.jboss.netty.handler.codec.http.HttpMethod._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.stream.ChunkedFile
import org.siny.web.elastic.index.ElasticController
import org.siny.web.model.{BookMark, User}
import org.siny.web.server.file.FileUtils._
import org.slf4j.LoggerFactory

import org.json4s.jackson.JsonMethods._
import org.json4s._



/**
 * Created by chengpohi on 4/18/15.
 */
class HttpResponseServerHandler extends SimpleChannelUpstreamHandler {
  lazy val LOG = LoggerFactory.getLogger(getClass.getName)
  implicit val formats = DefaultFormats
  val user = User("chengpohi")

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) = {
    val httpRequest = e.getMessage.asInstanceOf[HttpRequest]
    val uri = httpRequest.getUri
    LOG.info("VISIT URL: " + uri + " Method:" + httpRequest.getMethod)

    httpRequest.getMethod match {
      case GET => getListener(ctx, uri)
      case PUT => getListener(ctx, uri)
      case DELETE => deleteListener(ctx, httpRequest)
      case POST => postListener(ctx, httpRequest)
    }
  }


  def deleteListener(ctx: ChannelHandlerContext, httpRequest: HttpRequest): Unit = {
    val uriParts = httpRequest.getUri.split("/")
    uriParts match {
      case u: Array[String] if u.length == 3 && u(1) == ElasticController.BOOKMARK_TYPE =>
        ElasticController.deleteBookMarkById(u(2), user)
        writeBuffer(ctx.getChannel, ("Delete BookMark Ok, Id: " + u(2)).getBytes, OK)
      case _ =>
        writeBuffer(ctx.getChannel, "UNKNOWN URL VISIT".getBytes, BAD_REQUEST)
    }
  }

  def postListener(ctx: ChannelHandlerContext, httpRequest: HttpRequest): Unit = {
    val rawBookMark = httpRequest.getContent.toString(CharsetUtil.UTF_8)

    val bookMark = parse(rawBookMark).extract[BookMark]

    ElasticController.create(user, bookMark)
    writeBuffer(ctx.getChannel, "Create Success".getBytes, OK)
  }

  @throws(classOf[Exception])
  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent): Unit = {
    writeBuffer(ctx.getChannel, "Exception Has Occurred".getBytes, BAD_REQUEST)
    LOG.warn("Exception: " + e.getCause.getLocalizedMessage)
  }


  override def channelDisconnected(ctx: ChannelHandlerContext, e: ChannelStateEvent) = {
    super.channelDisconnected(ctx, e)
  }

  def getListener(ctx: ChannelHandlerContext, uri: String): Unit = {
    val uriParts = uri.split("/")
    uriParts match {
      case u: Array[String] if u.length > 1 && u(1) == ElasticController.BOOKMARK_TYPE =>
        writeBuffer(ctx.getChannel, ElasticController.getBookMarksWithJson(user).getBytes, OK)
      case _ =>
        uri.startsWith("http") match {
          case true => writeUrl(ctx.getChannel, uri)
          case false =>
            val file = getFile(uri)
            file.exists match {
              case true => writeFile(ctx.getChannel, file, OK)
              case false => writeBuffer(ctx.getChannel, (uri + ":" + " 404 Not Found").getBytes, NOT_FOUND)
            }
        }
    }
  }

  def writeUrl(channel: Channel, uri: String): Unit = {
    val conn = new URL(uri).openConnection()

    val is = conn.getInputStream

    val data = Stream.continually(is.read).takeWhile(-1 !=).map(_.toByte).toArray

    writeBuffer(channel, data, OK)
    is.close()
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

  def writeBuffer(channel: Channel, data: Array[Byte], status: HttpResponseStatus): Unit = {
    val response = new DefaultHttpResponse(HTTP_1_1, status)
    response.setContent(ChannelBuffers.wrappedBuffer(data))
    response.headers.add(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8")
    response.headers.add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
    response.headers.add(HttpHeaders.Names.CONTENT_LENGTH, response.getContent.readableBytes)

    channel.write(response)
  }
}
