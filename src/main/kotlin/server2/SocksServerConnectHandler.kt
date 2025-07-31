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

import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.socksx.SocksMessage
import io.netty.handler.codec.socksx.v4.DefaultSocks4CommandResponse
import io.netty.handler.codec.socksx.v4.Socks4CommandRequest
import io.netty.handler.codec.socksx.v4.Socks4CommandStatus
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.FutureListener
import server2.SocksServer.Companion.PORT
import io.netty.handler.ssl.SslHandler

@Sharable
class SocksServerConnectHandler : SimpleChannelInboundHandler<SocksMessage?>() {
    private val b = Bootstrap()

    @Throws(Exception::class)
    public override fun channelRead0(ctx: ChannelHandlerContext, message: SocksMessage?) {
        println("SocksServer : SocksServerConnectHandler channelRead0 : ${message}")

        if (message is Socks4CommandRequest) {
            handleSocks4Request(ctx, message)
        } else if (message is Socks5CommandRequest) {
            handleSocks5Request(ctx, message)
        } else {
            ctx.close()
        }
    }

    private fun handleSocks4Request(ctx: ChannelHandlerContext, request: Socks4CommandRequest) {
        val promise = ctx.executor().newPromise<Channel>()
        promise.addListener(
            object : FutureListener<Channel?> {
                @Throws(Exception::class)
                override fun operationComplete(future: Future<Channel?>) {
                    val outboundChannel = future.now
                    if (future.isSuccess) {
                        val responseFuture = ctx.channel().writeAndFlush(
                            DefaultSocks4CommandResponse(Socks4CommandStatus.SUCCESS)
                        )

                        responseFuture.addListener(ChannelFutureListener {
                            setupPipeline(ctx, outboundChannel, request.dstAddr(), request.dstPort())
                        })
                    } else {
                        ctx.channel().writeAndFlush(
                            DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED)
                        )
                        SocksServerUtils.closeOnFlush(ctx.channel())
                    }
                }
            })

        connectToTarget(ctx, request.dstAddr(), request.dstPort(), promise)
    }

    private fun handleSocks5Request(ctx: ChannelHandlerContext, request: Socks5CommandRequest) {
        val promise = ctx.executor().newPromise<Channel>()
        promise.addListener(
            object : FutureListener<Channel?> {
                @Throws(Exception::class)
                override fun operationComplete(future: Future<Channel?>) {
                    val outboundChannel = future.now
                    if (future.isSuccess) {
                        val responseFuture = ctx.channel().writeAndFlush(
                            DefaultSocks5CommandResponse(
                                Socks5CommandStatus.SUCCESS,
                                request.dstAddrType(),
                                request.dstAddr(),
                                request.dstPort()
                            )
                        )

                        responseFuture.addListener(ChannelFutureListener {
                            setupPipeline(ctx, outboundChannel, request.dstAddr(), request.dstPort())
                        })
                        responseFuture.addListener(ChannelFutureListener {
                            println("responseFuture.addListener operationComplete :: ${ctx.channel()}")
                        })
                    } else {
                        ctx.channel().writeAndFlush(
                            DefaultSocks5CommandResponse(
                                Socks5CommandStatus.FAILURE, request.dstAddrType()
                            )
                        )
                        SocksServerUtils.closeOnFlush(ctx.channel())
                    }
                }
            })

        connectToTarget(ctx, request.dstAddr(), request.dstPort(), promise)
    }

    private fun setupPipeline(ctx: ChannelHandlerContext, outboundChannel: Channel?, targetHost: String, targetPort: Int) {
        ctx.pipeline().remove(this@SocksServerConnectHandler)
        ctx.pipeline().addLast("relay", RelayHandler(outboundChannel))

        when (targetPort) {
            80, 8080 -> {
                // HTTP - добавляем HTTP-декодеры и модификатор
                outboundChannel?.pipeline()?.addLast(io.netty.handler.codec.http.HttpServerCodec())
                outboundChannel?.pipeline()?.addLast(io.netty.handler.codec.http.HttpObjectAggregator(1048576)) // Увеличиваем до 1MB
                outboundChannel?.pipeline()?.addLast(server2.HttpResponseModifier3(ctx.channel()))
            }
            443 -> {
                // HTTPS - MITM
                setupMitmPipeline(ctx, outboundChannel, targetHost)
            }
            else -> {
                // Другие протоколы - обычный прокси
                outboundChannel?.pipeline()?.addLast(ResponseHandler(ctx.channel()))
            }
        }
    }

    private fun setupMitmPipeline(ctx: ChannelHandlerContext, outboundChannel: Channel?, targetHost: String) {
        try {
            println("Setting up MITM pipeline for $targetHost")
            
            // 1. Генерируем SSL-контекст для клиента (поддельный сертификат)
            val clientSslContext = MitmCertUtil.generateServerCert(targetHost)
            if (clientSslContext != null) {
                println("Generated client SSL context for $targetHost")
                
                // 2. Добавляем SslHandler в пайплайн клиента
                val clientSslHandler = clientSslContext.newHandler(ctx.channel().alloc())
                ctx.pipeline().addFirst("ssl", clientSslHandler)
                println("Added SSL handler for client (MITM)")

                // 3. Создаем SSL-контекст для соединения с сервером
                val serverSslContext = MitmCertUtil.createClientSslContext()
                println("Created server SSL context (insecure)")
                val serverSslHandler = serverSslContext.newHandler(outboundChannel?.alloc())
                outboundChannel?.pipeline()?.addFirst("ssl", serverSslHandler)
                println("Added SSL handler for server connection")

                // 4. Добавляем HTTP-декодеры в пайплайн клиента
                ctx.pipeline().addLast(io.netty.handler.codec.http.HttpServerCodec())
                ctx.pipeline().addLast(io.netty.handler.codec.http.HttpObjectAggregator(1048576)) // 1MB
                println("Added HTTP decoders to client pipeline")
                
                // 5. Добавляем HTTP-декодеры в пайплайн сервера
                outboundChannel?.pipeline()?.addLast(io.netty.handler.codec.http.HttpClientCodec())
                outboundChannel?.pipeline()?.addLast(io.netty.handler.codec.http.HttpObjectAggregator(1048576)) // 1MB
                println("Added HTTP decoders to server pipeline")
                
                // 6. Добавляем SimpleMitmServerHandler в пайплайн сервера
                outboundChannel?.pipeline()?.addLast("simpleMitmServer", SimpleMitmServerHandler(ctx.channel()))
                println("Added SimpleMitmServerHandler to server pipeline")
                
                // 7. Заменяем RelayHandler на SimpleMitmHandler
                ctx.pipeline().remove("relay")
                ctx.pipeline().addLast("simpleMitm", SimpleMitmHandler(ctx.channel(), outboundChannel!!))
                println("Replaced RelayHandler with SimpleMitmHandler")
                
                println("MITM pipeline setup completed for $targetHost")
            } else {
                println("Failed to generate SSL context for $targetHost, falling back to tunnel mode")
                outboundChannel?.pipeline()?.addLast(ResponseHandler(ctx.channel()))
            }
        } catch (e: Exception) {
            println("Error setting up MITM for $targetHost: ${e.message}")
            e.printStackTrace()
            outboundChannel?.pipeline()?.addLast(ResponseHandler(ctx.channel()))
        }
    }

    private fun connectToTarget(ctx: ChannelHandlerContext, targetHost: String, targetPort: Int, promise: io.netty.util.concurrent.Promise<Channel>) {
        val inboundChannel = ctx.channel()
        b.group(inboundChannel.eventLoop())
            .channel(NioSocketChannel::class.java)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .handler(DirectClientHandler(promise))

        b.connect(targetHost, targetPort).addListener(ChannelFutureListener { future ->
            if (future.isSuccess) {
                println("Connected to target: $targetHost:$targetPort")
            } else {
                println("Failed to connect to target: $targetHost:$targetPort")
                ctx.channel().writeAndFlush(
                    DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, io.netty.handler.codec.socksx.v5.Socks5AddressType.DOMAIN)
                )
                SocksServerUtils.closeOnFlush(ctx.channel())
            }
        })
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        SocksServerUtils.closeOnFlush(ctx.channel())
    }
}