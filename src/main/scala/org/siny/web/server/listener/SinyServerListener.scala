package org.siny.web.server.listener

import java.io.{File, RandomAccessFile}
import java.net.URL

import org.elasticsearch.common.netty.buffer.ChannelBuffers
import org.elasticsearch.common.netty.channel.{Channel, ChannelHandlerContext}
import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus.{BAD_REQUEST, _}
import org.elasticsearch.common.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.elasticsearch.common.netty.handler.codec.http.{DefaultHttpResponse, HttpHeaders, HttpRequest, HttpResponseStatus}
import org.elasticsearch.common.netty.handler.stream.ChunkedFile
import org.elasticsearch.common.netty.util.CharsetUtil
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import org.siny.elastic.controller.ElasticController
import org.siny.file.FileUtils._
import org.siny.model.{BookMark, Tab, User}
import org.slf4j.LoggerFactory


/**
 * BookMark model
 * Created by chengpohi on 6/12/15.
 */
class SinyServerListener {
  val user = User("chengpohi")
  lazy val LOG = LoggerFactory.getLogger(getClass.getName)
  implicit val formats = DefaultFormats

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
    val uriParts = httpRequest.getUri.split("/")
    uriParts match {
      case u: Array[String] if u.length == 2 && u(1) == ElasticController.BOOKMARK_TYPE =>
        postBookMarkDealer(ctx, httpRequest)
      case u: Array[String] if u.length == 2 && u(1) == ElasticController.TAB_TYPE =>
        postTab(ctx, httpRequest)
      case _ =>
    }
  }

  def postTab(ctx: ChannelHandlerContext, httpRequest: HttpRequest): Unit = {
    val rawTab = httpRequest.getContent.toString(CharsetUtil.UTF_8)

    val tab = parse(rawTab).extract[Tab]

    val resultId = ElasticController.createTab(user, tab)
    writeBuffer(ctx.getChannel, resultId.getBytes, OK)
  }

  def postBookMarkDealer(ctx: ChannelHandlerContext, httpRequest: HttpRequest): Unit = {
    val rawBookMark = httpRequest.getContent.toString(CharsetUtil.UTF_8)
    httpRequest.headers()

    val bookMark = parse(rawBookMark).extract[BookMark]

    ElasticController.createBookMark(user, bookMark)
    writeBuffer(ctx.getChannel, "Create Success".getBytes, OK)
  }

  def getListener(ctx: ChannelHandlerContext, uri: String): Unit = {
    val uriParts = uri.split("/")
    uriParts match {
      case u: Array[String] if u.length > 1 && u(1) == ElasticController.BOOKMARK_TYPE =>
        writeBuffer(ctx.getChannel, ElasticController.getBookMarksWithJson(user).getBytes, OK)
      case _ =>
        uri.startsWith("http") match {
          case true => //writeUrl(ctx.getChannel, uri)
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
