package org.siny.web.server.handler


import com.secer.elastic.model.User
import org.elasticsearch.common.netty.channel._
import org.elasticsearch.common.netty.handler.codec.http.{HttpHeaders, HttpRequest}
import org.elasticsearch.common.netty.handler.codec.http.HttpMethod._
import org.elasticsearch.common.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST
import org.siny.web.server.cache.LoginUserCache
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

    val cookies = for {
      u <- httpRequest.headers().get(HttpHeaders.Names.COOKIE).split(";")
      if u.startsWith("cookieId")
    } yield u

    LoginUserCache.LOGINED_USER.getOrElse(cookies(0), null) match {
      case null => {
        httpRequest.setUri("/login.html")
        sinyServerListener.getListener(ctx, HttpSession(null, httpRequest))
      }
      case u: User => {
        val httpSession = HttpSession(u, httpRequest)

        val uri = httpRequest.getUri

        LOG.info("IP: " + e.getRemoteAddress + " VISIT URL: " + uri + " Method:" + httpRequest.getMethod)
        httpRequest.getMethod match {
          case GET => sinyServerListener.getListener(ctx, httpSession)
          case PUT => sinyServerListener.getListener(ctx, httpSession)
          case DELETE => sinyServerListener.deleteListener(ctx, httpSession)
          case POST => sinyServerListener.postListener(ctx, httpSession)
        }
      }
    }
  }

  @throws(classOf[Exception])
  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent): Unit = {
    sinyServerListener.writeBuffer(ctx.getChannel, "Exception has occurred".getBytes, BAD_REQUEST)
    LOG.warn("Exception: " + e.getCause.getLocalizedMessage)
  }


  override def channelDisconnected(ctx: ChannelHandlerContext, e: ChannelStateEvent) = {
    super.channelDisconnected(ctx, e)
  }
}
