package org.siny.web.server.handler


import org.elasticsearch.common.netty.channel._
import org.elasticsearch.common.netty.handler.codec.http.HttpMethod._
import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST
import org.elasticsearch.common.netty.handler.codec.http.{HttpHeaders, HttpRequest}
import org.siny.web.server.cache.LoginUserCache
import org.siny.web.server.helper.RequestPath
import org.siny.web.server.listener.SinyServerListener
import org.siny.web.server.session.HttpSession
import org.slf4j.LoggerFactory

/**
 * Created by chengpohi on 4/18/15.
 */
class HttpResponseSinyServerHandler extends SimpleChannelUpstreamHandler {
  lazy val LOG = LoggerFactory.getLogger(getClass.getName)
  lazy val sinyServerListener = new SinyServerListener

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) = {
    val httpRequest = e.getMessage.asInstanceOf[HttpRequest]

    LOG.info("IP: " + e.getRemoteAddress + " VISIT URL: " + httpRequest.getUri + " Method:" + httpRequest.getMethod)

    httpRequest.getUri == "/" + RequestPath.LOGIN && httpRequest.getMethod == POST match {
      case true =>
        val httpSession = HttpSession(null, httpRequest)
        sinyServerListener.postListener(ctx, httpSession)
      case false => visitHandler(ctx, httpRequest)
    }

  }

  def visitHandler(ctx: ChannelHandlerContext, httpRequest: HttpRequest): Unit = {
    val user = for {
      c <- httpRequest.headers().get(HttpHeaders.Names.COOKIE).split(";")
      if c.trim.matches("cookieID=.+")
    } yield LoginUserCache.LOGINED_USER.getOrElse(c.split("=")(1), null)

    user match {
      case u if u.length == 1 && u(0) != null =>
        val u = user(0)
        val uri = httpRequest.getUri

        if (uri.equals("/" + RequestPath.LOGIN))
          httpRequest.setUri("/")

        val httpSession = HttpSession(u, httpRequest)

        httpRequest.getMethod match {
          case GET => sinyServerListener.getListener(ctx, httpSession)
          case PUT => sinyServerListener.getListener(ctx, httpSession)
          case DELETE => sinyServerListener.deleteListener(ctx, httpSession)
          case POST => sinyServerListener.postListener(ctx, httpSession)
        }
      case _ =>
        val uri = httpRequest.getUri
        uri match {
          case path: String if path.startsWith("/assets") =>
            sinyServerListener.getListener(ctx, HttpSession(null, httpRequest))
          case _ =>
            httpRequest.setUri("/login.html")
            sinyServerListener.getListener(ctx, HttpSession(null, httpRequest))
        }
    }
  }

  @throws(classOf[Exception])
  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent): Unit = {
    sinyServerListener.writeBuffer(ctx.getChannel, "Internal Exception has occurred".getBytes, BAD_REQUEST)
    LOG.warn("Exception: " + e.toString)
  }


  override def channelDisconnected(ctx: ChannelHandlerContext, e: ChannelStateEvent) = {
    super.channelDisconnected(ctx, e)
  }
}
