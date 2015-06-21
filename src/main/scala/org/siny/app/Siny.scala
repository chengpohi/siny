package org.siny.app

import java.net.InetSocketAddress
import java.util.concurrent.Executors

import com.typesafe.config.ConfigFactory

import org.elasticsearch.common.netty.bootstrap.ServerBootstrap
import org.elasticsearch.common.netty.channel.{ChannelPipeline, Channels, ChannelPipelineFactory}
import org.elasticsearch.common.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.elasticsearch.common.netty.handler.codec.http.{HttpResponseEncoder, HttpChunkAggregator, HttpRequestDecoder}
import org.elasticsearch.common.netty.handler.stream.ChunkedWriteHandler


import org.slf4j.LoggerFactory

import org.siny.web.server.handler.HttpResponseSinyServerHandler

/**
 * Created by xiachen on 4/17/15.
 */
object Siny {
  def main(args: Array[String]) {
    lazy val config = ConfigFactory.load()
    lazy val LOG = LoggerFactory.getLogger(getClass.getName)
    val serverBootStrap = new ServerBootstrap(new NioServerSocketChannelFactory(
      Executors.newCachedThreadPool(),
      Executors.newCachedThreadPool()
    ))

    serverBootStrap.setPipelineFactory(new ChannelPipelineFactory() {
      override def getPipeline: ChannelPipeline = {
        val pipeline: ChannelPipeline = Channels.pipeline()

        pipeline.addLast("decoder", new HttpRequestDecoder())
        pipeline.addLast("aggregator", new HttpChunkAggregator(65536))
        pipeline.addLast("encoder", new HttpResponseEncoder())
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler())

        pipeline.addLast("handler", new HttpResponseSinyServerHandler())
        pipeline
      }
    })

    serverBootStrap.setOption("child.reuseAddress", true)
    serverBootStrap.setOption("child.tcpNoDelay", true)
    serverBootStrap.setOption("child.keepAlive", true)

    // Bind and start to accept incoming connections.
    serverBootStrap.bind(new InetSocketAddress(config.getInt("http.port")))
    LOG.info("Server Started, IP: " + config.getString("http.interface") + ", Port: " + config.getInt("http.port"))
  }
}

