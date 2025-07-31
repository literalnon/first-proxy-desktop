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

import io.netty.buffer.AbstractReferenceCountedByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.CharsetUtil
import io.netty.util.ReferenceCountUtil
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class ResponseHandler(private val relayChannel: Channel?) : ChannelInboundHandlerAdapter() {
    //private val
    override fun channelActive(ctx: ChannelHandlerContext) {
        println("SocksServer : ResponseHandler channelActive")
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        println("SocksServer : ResponseHandler channelRead : ${ctx} :: ${msg}")
        if (msg is AbstractReferenceCountedByteBuf) {
            //println("SocksServer : ResponseHandler channelRead 2 :: ${msg.toString(CharsetUtil.US_ASCII)}")
        }

        if (relayChannel?.isActive == true) {
            relayChannel.writeAndFlush(msg)
            //relayChannel.writeAndFlush(Unpooled.copiedBuffer("Hello, Netty!", StandardCharsets.UTF_8));
        } else {
            ReferenceCountUtil.release(msg)
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        println("SocksServer : ResponseHandler channelInactive")
        if (relayChannel?.isActive == true) {
            SocksServerUtils.closeOnFlush(relayChannel)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}