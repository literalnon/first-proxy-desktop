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
package server2

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import server2.SocksServerHandler.Companion.INSTANCE
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.util.CharsetUtil
import io.netty.buffer.Unpooled
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.buffer.ByteBuf
import server.encode

class SocksServerInitializer : ChannelInitializer<SocketChannel>() {
    @Throws(Exception::class)
    public override fun initChannel(ch: SocketChannel) {
        println("SocksServer : SocksServerInitializer initChannel")
        ch.pipeline()
            .addLast(
                LoggingHandler(LogLevel.DEBUG),
                SocksPortUnificationServerHandler(),
                //ProtocolDetectingHandler(),
                INSTANCE
            )
    }
}

//class HttpResponseModifier3 : ChannelInboundHandlerAdapter() {
//    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
//        println("TEST :: HttpResponseModifier3 :: channelRead 0")
//        if (msg is FullHttpResponse) {
//            val content = msg.content().toString(CharsetUtil.UTF_8)
//            var modifiedContent = content.replace("Футбол", "баскетбол")
//                .replace("Футбол".encode(), "баскетбол")
//            val newContent = Unpooled.copiedBuffer(modifiedContent, CharsetUtil.UTF_8)
//            msg.content().clear().writeBytes(newContent)
//            ctx.fireChannelRead(msg)
//            println("TEST :: HttpResponseModifier3 :: channelRead :: ${content}")
//        } else {
//            ctx.fireChannelRead(msg)
//        }
//    }
//}

class ProtocolDetectingHandler : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        println("TEST :: ProtocolDetectingHandler :: decode")
        if (looksLikeHttp(input)) {
            println("TEST :: ProtocolDetectingHandler :: decode :: true")
            // Добавляем HTTP-декодеры и ваш обработчик
            ctx.pipeline().addLast(HttpServerCodec())
            ctx.pipeline().addLast(HttpObjectAggregator(65536))
            //ctx.pipeline().addLast(HttpResponseModifier3())
            ctx.pipeline().remove(this)
        } else {
            // Не HTTP — удаляем себя и продолжаем обычную обработку
            ctx.pipeline().remove(this)
        }
    }

    private fun looksLikeHttp(buf: ByteBuf): Boolean {
        // Простейшая проверка: начинается с "GET", "POST", "HTTP" и т.д.
        val magic = buf.toString(buf.readerIndex(), buf.readableBytes().coerceAtMost(4), CharsetUtil.US_ASCII)
        println("TEST :: ProtocolDetectingHandler :: looksLikeHttp :: ${magic} :: ${buf.toString(buf.readerIndex(), buf.readableBytes(), CharsetUtil.US_ASCII)}")
        return magic.startsWith("GET") || magic.startsWith("POST") || magic.startsWith("HTTP")
    }
}