package org.siny.web.handler

import com.secer.elastic.model.User
import org.elasticsearch.common.netty.channel._
import org.elasticsearch.common.netty.handler.codec.http.HttpMethod.{DELETE, GET, POST, PUT}
import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST
import org.elasticsearch.common.netty.handler.codec.http.{HttpHeaders, HttpRequest}
import org.elasticsearch.common.netty.util.CharsetUtil
import org.json4s._
import org.json4s.native.JsonMethods._
import org.siny.web.cache.LoginUserCache
import org.siny.web.response.HttpResponse
import org.siny.web.response.ResponseWriter.{writeBuffer, writeFile}
import org.siny.web.rest.controller.RestController._
import org.siny.web.session.HttpSession
import org.slf4j.LoggerFactory

/**
 * Rest Server Handler to deal Http Request
 * Created by chengpohi on 4/18/15.
 */
class RestServerHandler extends SimpleChannelUpstreamHandler {
  lazy val LOG = LoggerFactory.getLogger(getClass.getName)


  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) = {
    val httpRequest = e.getMessage.asInstanceOf[HttpRequest]

    val user = visitHandler(ctx, httpRequest)


    val uri: String = httpRequest.getUri

    LOG.info("IP: " + e.getRemoteAddress + " VISIT URL: " + uri + " Method:" + httpRequest.getMethod)
    val f: DealerType = httpRequest.getMethod match {
      case GET => getActions.getOrElse(uri, null)
      case POST => postActions.getOrElse(uri, null)
      case PUT => putActions.getOrElse(uri, null)
      case DELETE => deleteActions.getOrElse(uri, null)
      case _ => null
    }

    val httpSession = HttpSession(user, httpRequest)

    f match {
      case null =>
        writeFile(ctx.getChannel, uri, httpSession)
      case _ =>
        val httpResponse: HttpResponse = executehandler(f, httpSession)
        writeBuffer(ctx.getChannel, httpResponse)
    }
  }

  def executehandler(f: DealerType, httpSession: HttpSession): HttpResponse = httpSession.httpRequest.getMethod match {
    case GET =>
      f(httpSession)
    case POST =>
      val rawData = httpSession.httpRequest.getContent.toString(CharsetUtil.UTF_8)
      f(parse(rawData).extract)
    case PUT =>
      f(httpSession)
    case DELETE =>
      f(httpSession)
  }

  def visitHandler(ctx: ChannelHandlerContext, httpRequest: HttpRequest): User = {
    val user = for {
      c <- httpRequest.headers().get(HttpHeaders.Names.COOKIE).split(";")
      if c.trim.matches("cookieID=.+")
    } yield LoginUserCache.LOGINED_USER.getOrElse(c.split("=")(1), null)

    user match {
      case u if u.length == 1 && u(0) != null =>
        user(0)
      case _ =>
        null
    }
  }

  @throws(classOf[Exception])
  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent): Unit = {
    LOG.warn("Exception: " + e.toString)
    writeBuffer(ctx.getChannel, "Internal Exception has occurred".getBytes, BAD_REQUEST)
  }


  override def channelDisconnected(ctx: ChannelHandlerContext, e: ChannelStateEvent) = {
    super.channelDisconnected(ctx, e)
  }
}
