package org.siny.web.server

import java.net.InetSocketAddress
import java.util.concurrent.Executors

import com.typesafe.config.ConfigFactory
import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.channel.{ChannelPipeline, ChannelPipelineFactory, Channels}
import org.jboss.netty.handler.codec.http.{HttpChunkAggregator, HttpRequestDecoder, HttpResponseEncoder}
import org.jboss.netty.handler.stream.ChunkedWriteHandler

/**
 * Created by xiachen on 4/17/15.
 */
object Server extends App {
  val config = ConfigFactory.load()
  val serverBootStrap = new ServerBootstrap(new NioServerSocketChannelFactory(
    Executors.newCachedThreadPool(),
    Executors.newCachedThreadPool()
  ))

  // Set up the event pipeline factory.
  serverBootStrap.setPipelineFactory(new ChannelPipelineFactory() {
    override def getPipeline: ChannelPipeline = {
      val pipeline: ChannelPipeline = Channels.pipeline()

      pipeline.addLast("decoder", new HttpRequestDecoder())
      pipeline.addLast("aggregator", new HttpChunkAggregator(65536))
      pipeline.addLast("encoder", new HttpResponseEncoder())
      pipeline.addLast("chunkedWriter", new ChunkedWriteHandler())

      pipeline.addLast("handler", new HttpChunkedServerHandler())
      pipeline
    }
  })

  serverBootStrap.setOption("child.reuseAddress", true)
  serverBootStrap.setOption("child.tcpNoDelay", true)
  serverBootStrap.setOption("child.keepAlive", true)

  // Bind and start to accept incoming connections.
  serverBootStrap.bind(new InetSocketAddress(config.getInt("http.port")))
}

