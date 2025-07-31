/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package server2;

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.util.CharsetUtil
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.buffer.Unpooled

class SocksServer {
    @Throws(Exception::class)
    fun start() {
        println("SocksServer : start")
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val b = ServerBootstrap()
            b.group(group)
                .channel(NioServerSocketChannel::class.java)
                .handler(LoggingHandler(LogLevel.INFO))
                .childHandler(SocksServerInitializer())
            b.bind(PORT).sync().channel().closeFuture().sync()
        } finally {
            group.shutdownGracefully()
        }
    }

    companion object {
        const val PORT: Int = 1080 //Integer.parseInt(System.getProperty("port", "1080"));
    }
}

class HttpResponseModifier3(private val relayChannel: Channel?) : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        println("HttpResponseModifier3 :: channelRead :: $msg")
        if (msg is FullHttpResponse) {
            val content = msg.content().toString(CharsetUtil.UTF_8)
            val modifiedContent = content.replace("футбол", "баскетбол")
            val newContent = Unpooled.copiedBuffer(modifiedContent, CharsetUtil.UTF_8)
            msg.content().clear().writeBytes(newContent)
            relayChannel?.writeAndFlush(msg)
            println("Modified HTTP response and sent to client")
        } else {
            relayChannel?.writeAndFlush(msg)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        println("HttpResponseModifier3 exception: ${cause.message}")
        cause.printStackTrace()
        ctx.close()
    }
}