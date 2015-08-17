package org.siny.web.response

import java.io.{File, RandomAccessFile}
import java.net.URL

import org.elasticsearch.common.netty.buffer.ChannelBuffers
import org.elasticsearch.common.netty.channel.Channel
import org.elasticsearch.common.netty.handler.codec.http.HttpHeaders.Names.{CONNECTION, CONTENT_LENGTH, CONTENT_TYPE, SET_COOKIE}
import org.elasticsearch.common.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE
import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus._
import org.elasticsearch.common.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.elasticsearch.common.netty.handler.codec.http.{DefaultHttpResponse, HttpResponseStatus}
import org.elasticsearch.common.netty.handler.stream.ChunkedFile
import org.siny.web.file.FileUtils
import FileUtils._
import org.siny.web.session.HttpSession

/**
 * siny
 * Created by chengpohi on 8/15/15.
 */
object ResponseWriter {
  def writeProxy(channel: Channel, uri: String): Unit = {
    val conn = new URL(uri).openConnection()

    val is = conn.getInputStream

    val data = Stream.continually(is.read).takeWhile(-1 !=).map(_.toByte).toArray

    writeBuffer(channel, data, OK)
    is.close()
  }

  def writeFile(channel: Channel, uri: String, httpSession: HttpSession): Unit = {
    val file = getWebFile(uri)
    file.exists match {
      case true => writeFile(channel, file, OK)
      case false => writeBuffer(channel, (uri + ":" + " 404 Not Found").getBytes, NOT_FOUND)
    }
  }

  def writeFile(channel: Channel, file: File, status: HttpResponseStatus): Unit = {
    val indexFile = new RandomAccessFile(file, "r")

    val response = new DefaultHttpResponse(HTTP_1_1, status)
    response.headers.add(CONTENT_TYPE, "text/html; charset=UTF-8")
    response.headers.add(CONNECTION, KEEP_ALIVE)
    response.headers.add(CONTENT_LENGTH, indexFile.length())

    channel.write(response)

    channel.write(new ChunkedFile(indexFile, 0, indexFile.length(), 8192))
  }

  def writeBuffer(channel: Channel, httpResponse: HttpResponse): Unit = {
    writeBuffer(channel, httpResponse.content.getBytes, httpResponse.status, httpResponse.cookie.getOrElse(""))
  }

  def writeBuffer(channel: Channel, data: Array[Byte], status: HttpResponseStatus): Unit = {
    writeBuffer(channel, data, status, "")
  }

  def writeBuffer(channel: Channel, data: Array[Byte], status: HttpResponseStatus, cookieId: String): Unit = {
    val response = new DefaultHttpResponse(HTTP_1_1, status)
    response.setContent(ChannelBuffers.wrappedBuffer(data))
    response.headers.add(CONTENT_TYPE, "text/html; charset=UTF-8")
    response.headers.add(CONNECTION, KEEP_ALIVE)
    response.headers.add(CONTENT_LENGTH, response.getContent.readableBytes)
    cookieId match {
      case s if s.length != 0 =>
        response.headers.add(SET_COOKIE, s"cookieID=$cookieId")
      case _ =>
    }

    channel.write(response)
  }

}
