package cn.mmooo

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.*
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import org.slf4j.LoggerFactory


/**
 * socket服务端
 */
class NettyServerNew {

    companion object {
        private val logger = LoggerFactory.getLogger(NettyServerNew::class.java)
        const val PORT = 9998
    }


    fun run() {
        val bossGroup: EventLoopGroup = MultiThreadIoEventLoopGroup(NioIoHandler.newFactory())
        val workerGroup: EventLoopGroup = MultiThreadIoEventLoopGroup(NioIoHandler.newFactory())
        try {
            val b = ServerBootstrap()
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    @Throws(Exception::class)
                    override fun initChannel(channel: SocketChannel) {
                        //获取责任链对象
                        val pipeline = channel.pipeline().addLast("logger", LoggingHandler(LogLevel.INFO)) // 调试器
                            .addLast(HttpServerCodec()) // HTTP 协议解析，用于握手阶段
                            .addLast(HttpObjectAggregator(65536)) // HTTP 协议解析，用于握手阶段
                            .addLast(WebSocketServerCompressionHandler()) // WebSocket 数据压缩扩展
                            .addLast(WebSocketServerProtocolHandler("/", null, true)) // WebSocket 握手、控制帧处理

                        pipeline.addLast(MyWebSocketServerHandler())
                    }
                })

            val f = b.bind(PORT).sync()
            logger.info("websocket service has been started at $PORT")
            f.channel().closeFuture().sync()
        } catch (e: Exception) {
            logger.error("websocket service failed to start", e)
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }


    @Sharable
    class MyWebSocketServerHandler : SimpleChannelInboundHandler<WebSocketFrame?>() {
        @Throws(java.lang.Exception::class)
        override fun channelRead0(ctx: ChannelHandlerContext, frame: WebSocketFrame?) {
            when (frame) {
                is TextWebSocketFrame -> { // 此处仅处理 Text Frame
                    logger.info("TextWebSocketFrame $frame")
                    val request = frame.text()
                    ctx.channel().writeAndFlush(TextWebSocketFrame("收到: $request"))
                }

                is PingWebSocketFrame -> {
                    logger.info("PING")
                    ctx.channel().writeAndFlush(PongWebSocketFrame())
                }

                is BinaryWebSocketFrame -> {
                    logger.info("BinaryWebSocketFrame ${frame}")
                }

                is ContinuationWebSocketFrame -> {
                    logger.info("ContinuationWebSocketFrame ${frame}")
                }
            }
        }
    }


}

fun main() {
    NettyServerNew().run()
}
