package org.siny.web.server.handler

import java.net.InetSocketAddress
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit._

import org.jboss.netty.bootstrap.{ClientBootstrap, ServerBootstrap}
import org.jboss.netty.buffer.ChannelBuffers._
import org.jboss.netty.channel._
import org.jboss.netty.channel.socket.nio.{NioClientSocketChannelFactory, NioServerSocketChannelFactory}
import org.jboss.netty.handler.codec.http.HttpHeaders.Names._
import org.jboss.netty.handler.codec.http.HttpHeaders.Values._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion._
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.util.CharsetUtil._
import org.jboss.netty.util.{HashedWheelTimer, Timeout, TimerTask}
import org.scalatest.FlatSpec

/**
 * Created by xiachen on 4/19/15.
 */
class HttpResponseServerHandlerTest extends FlatSpec {
  val RESPONSE_TIMEOUT = 10000L
  val CONNECTION_TIMEOUT = 10000L
  val CONTENT_TYPE_TEXT = "text/plain charset=UTF-8"
  val HOST_ADDR = new InetSocketAddress("127.0.0.1", 9080)
  val PATH1 = "/1"
  val PATH2 = "/2"
  val SOME_RESPONSE_TEXT = "some response for "
  var responses = List[String]()

  val responsesIn: CountDownLatch = new CountDownLatch(0)

  val timer: HashedWheelTimer = new HashedWheelTimer()

  val serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory())

  val clientBootstrap = new ClientBootstrap(new NioClientSocketChannelFactory())

  clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
    override def getPipeline = {
      Channels.pipeline(
        new HttpClientCodec(),
        new ClientHandler()
      )
    }
  })

  serverBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
    @throws(classOf[Exception])
    override def getPipeline: ChannelPipeline = Channels.pipeline(
      new HttpRequestDecoder(),
      new HttpResponseEncoder(),
      new ServerHandler()
    )
  })

  serverBootstrap.bind(HOST_ADDR)

  "client " should "connect server" in {
    val connectionFuture = clientBootstrap.connect(HOST_ADDR)
    connectionFuture.await(CONNECTION_TIMEOUT)

    val clientChannel = connectionFuture.getChannel

    val request1 = new DefaultHttpRequest(HTTP_1_1, HttpMethod.GET, PATH1)
    clientChannel.write(request1)

    Thread.sleep(RESPONSE_TIMEOUT)
    assert(responses(0).contains(SOME_RESPONSE_TEXT))
  }

  class ServerHandler extends SimpleChannelUpstreamHandler {
    @throws(classOf[InterruptedException])
    override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
      val request: HttpRequest = e.getMessage.asInstanceOf[HttpRequest]
      val oue = e.asInstanceOf[UpstreamMessageEvent]

      val initialChunk: HttpResponse = new DefaultHttpResponse(HTTP_1_1, OK)
      initialChunk.headers.add(CONTENT_TYPE, CONTENT_TYPE_TEXT)
      initialChunk.headers.add(CONNECTION, KEEP_ALIVE)
      initialChunk.headers.add(TRANSFER_ENCODING, CHUNKED)

      ctx.sendDownstream(new DownstreamMessageEvent(oue.getChannel, Channels.future(oue.getChannel),
        initialChunk, oue.getRemoteAddress))

      timer.newTimeout(new ChunkWriter(ctx, e, request.getUri, oue, 1), 0, MILLISECONDS)
    }

    class ChunkWriter(ctx: ChannelHandlerContext, e: MessageEvent, uri: String, oue: UpstreamMessageEvent, subSequence: Int) extends TimerTask {
      override def run(timeout: Timeout): Unit = subSequence match {
        case s: Int if s > 1 =>
          val chunk = new DefaultHttpChunk(EMPTY_BUFFER)
          ctx.sendDownstream(new DownstreamMessageEvent(oue.getChannel, Channels.future(oue.getChannel), chunk, oue.getRemoteAddress))
        case _ =>
          val chunk = new DefaultHttpChunk(copiedBuffer(SOME_RESPONSE_TEXT + uri, UTF_8))
          ctx.sendDownstream(new DownstreamMessageEvent(oue.getChannel, Channels.future(oue.getChannel), chunk, oue.getRemoteAddress))
          timer.newTimeout(new ChunkWriter(ctx, e, uri, oue, subSequence + 1), 0, MILLISECONDS)
      }
    }
  }

  class ClientHandler extends SimpleChannelUpstreamHandler {
    override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) = e.getMessage match {
      case response: HttpChunk =>
        if (!response.isLast) {
          val content = response.getContent.toString(UTF_8)
          responses = responses :+ content
        }
      case _ =>
    }
  }

}
