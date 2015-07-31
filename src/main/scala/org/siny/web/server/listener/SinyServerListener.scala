package org.siny.web.server.listener

import java.io.{File, RandomAccessFile}
import java.net.URL

import com.secer.elastic.controller.ElasticController._
import com.secer.elastic.controller.UserController
import com.secer.elastic.model.{BookMark, Tab, User}
import org.elasticsearch.common.netty.buffer.ChannelBuffers
import org.elasticsearch.common.netty.channel.{Channel, ChannelHandlerContext}
import org.elasticsearch.common.netty.handler.codec.http.HttpHeaders.Names.{CONNECTION, CONTENT_LENGTH, CONTENT_TYPE, SET_COOKIE}
import org.elasticsearch.common.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE
import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus.{BAD_REQUEST, _}
import org.elasticsearch.common.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.elasticsearch.common.netty.handler.codec.http.{DefaultHttpResponse, HttpRequest, HttpResponseStatus}
import org.elasticsearch.common.netty.handler.stream.ChunkedFile
import org.elasticsearch.common.netty.util.CharsetUtil
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import org.siny.file.FileUtils._
import org.siny.web.server.cache.LoginUserCache.LOGINED_USER
import org.siny.web.server.helper.RequestPath
import org.slf4j.LoggerFactory


/**
 * Siny Server Listener
 * Created by chengpohi on 6/12/15.
 */
class SinyServerListener {
  lazy val LOG = LoggerFactory.getLogger(getClass.getName)
  implicit val formats = DefaultFormats

  val USER = User("chengpohi")
  val LOGIN_INPUT_TIP: String = "Please input email with password!"
  val LOGIN_ERROR_TIP: String = "Wrong Email or Password."
  val LOGIN_SUCCESSFUL_TIP: String = "Login Successfully"

  def deleteListener(ctx: ChannelHandlerContext, httpRequest: HttpRequest): Unit = {
    val uriParts = httpRequest.getUri.split("/")
    uriParts match {
      case u: Array[String] if u.length == 3 && u(1) == BOOKMARK_TYPE =>
        deleteBookMarkById(u(2), USER)
        writeBuffer(ctx.getChannel, ("Delete BookMark Ok, Id: " + u(2)).getBytes, OK)
      case _ =>
        writeBuffer(ctx.getChannel, "UNKNOWN URL VISIT".getBytes, BAD_REQUEST)
    }
  }


  def postListener(ctx: ChannelHandlerContext, httpRequest: HttpRequest): Unit = {
    val uriParts = httpRequest.getUri.split("/")
    uriParts match {
      case u: Array[String] if u.length == 2 && u(1) == BOOKMARK_TYPE =>
        postBookMarkDealer(ctx, httpRequest)
      case u: Array[String] if u.length == 2 && u(1) == TAB_TYPE =>
        postTab(ctx, httpRequest)
      case u: Array[String] if u.length == 2 && u(1) == RequestPath.LOGIN =>
        userLogin(ctx, httpRequest)
      case u: Array[String] if u.length == 2 && u(1) == RequestPath.REGISTER =>
        registerUser(ctx, httpRequest)
      case _ =>
    }
  }

  def postTab(ctx: ChannelHandlerContext, httpRequest: HttpRequest): Unit = {
    val rawTab = httpRequest.getContent.toString(CharsetUtil.UTF_8)

    val tab = parse(rawTab).extract[Tab]

    val resultId = createTab(USER, tab)
    writeBuffer(ctx.getChannel, resultId.getBytes, OK)
  }

  def postBookMarkDealer(ctx: ChannelHandlerContext, httpRequest: HttpRequest): Unit = {
    val rawBookMark = httpRequest.getContent.toString(CharsetUtil.UTF_8)

    val bookMark = parse(rawBookMark).extract[BookMark]

    createBookMark(USER, bookMark)
    writeBuffer(ctx.getChannel, "Create Success".getBytes, OK)
  }

  def userLogin(ctx: ChannelHandlerContext, httpRequest: HttpRequest): Unit = {
    val rawUser = httpRequest.getContent.toString(CharsetUtil.UTF_8).split("&")

    def validateUser(): Unit = {
      val email = rawUser(0).split("=")(1)
      val password = rawUser(1).split("=")(1)

      val user = User("", Option(email), Option(password))
      UserController.validateUserLogin(user) match {
        case u: User => {
          LOGINED_USER += u
          writeBuffer(ctx.getChannel, LOGIN_SUCCESSFUL_TIP.getBytes, OK, u.cookie.get)
        }
        case null =>
          writeBuffer(ctx.getChannel, LOGIN_ERROR_TIP.getBytes, BAD_REQUEST)
      }
    }

    rawUser(0).endsWith("=") || rawUser(1).endsWith("=") match {
      case true =>
        writeBuffer(ctx.getChannel, LOGIN_INPUT_TIP.getBytes, BAD_REQUEST)
      case false => validateUser()
    }
  }

  def registerUser(ctx: ChannelHandlerContext, httpRequest: HttpRequest): Unit = {
    val rawUser = httpRequest.getContent.toString(CharsetUtil.UTF_8)

    val user = parse(rawUser).extract[User]

    val resultId = UserController.createUserInfo(user)
    writeBuffer(ctx.getChannel, resultId.getBytes, OK)
  }

  def getListener(ctx: ChannelHandlerContext, uri: String): Unit = {
    val uriParts = uri.split("/")
    uriParts match {
      case u: Array[String] if u.length > 1 && u(1) == BOOKMARK_TYPE =>
        writeBuffer(ctx.getChannel, getBookMarksWithJson(USER).getBytes, OK)
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
    response.headers.add(CONTENT_TYPE, "text/html; charset=UTF-8")
    response.headers.add(CONNECTION, KEEP_ALIVE)
    response.headers.add(CONTENT_LENGTH, indexFile.length())

    channel.write(response)

    channel.write(new ChunkedFile(indexFile, 0, indexFile.length(), 8192))
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
    response.headers.add(SET_COOKIE, s"cookieID=$cookieId")

    channel.write(response)
  }

}
