package server2

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.DefaultFullHttpRequest
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler
import io.netty.handler.codec.socksx.v5.*
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import java.nio.charset.StandardCharsets


class MitmSocksProxy {
    fun start() {

        println("MitmSocksProxy : start")
        val bossGroup: NioEventLoopGroup = NioEventLoopGroup(1)
        val workerGroup: NioEventLoopGroup = NioEventLoopGroup()

        try {
            ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(MitmSocksInitializer())
                .bind(1080)
                .sync()
                .channel()
                .closeFuture()
                .sync()

            println("MitmSocksProxy : started")
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }
}

class MitmSocksInitializer : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        println("MitmSocksInitializer : initChannel")

        ch.pipeline().addLast( // SOCKS5
            // Логирование (для отладки)
            LoggingHandler(LogLevel.DEBUG),

            // SOCKS5 Handshake
            Socks5InitialRequestDecoder(),
            Socks5ServerEncoder.DEFAULT,

//            Socks5ServerEncoder.DEFAULT,
//            Socks5InitialRequestDecoder(),
//            Socks5CommandRequestDecoder(),  // После успешного SOCKS5-подключения:

            // Обработчик InitialRequest
            //Socks5InitialResponseHandler(),
            // Декодеры для последующих этапов
            Socks5CommandRequestDecoder(),

            HttpServerCodec(),  // Декодирует HTTP
            HttpObjectAggregator(65536),  // Объединяет части запроса
            TextReplacerHandler()
        )
    }
}

class Socks5InitialResponseHandler

    : SimpleChannelInboundHandler<Socks5InitialRequest?>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Socks5InitialRequest?) {
        // Отправляем ответ: "Без аутентификации" (0x05 0x00)
        ctx.writeAndFlush(DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH))
    }
}

class TextReplacerHandler : ChannelInboundHandlerAdapter() {
    // В начало TextReplacerHandler:
    override fun channelActive(ctx: ChannelHandlerContext) {
        println("Клиент подключен: " + ctx.channel().remoteAddress())
        super.channelActive(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        println("TextReplacerHandler : channelRead : msg : ${msg}")
        if (msg is Socks5InitialRequest) {
            println("TextReplacerHandler : msg is Socks5InitialRequest")
            ctx.writeAndFlush(DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH))
            return
        } else if (msg is Socks5CommandRequest) {
            println("TextReplacerHandler : msg is Socks5CommandRequest : ${msg.type()}")
            // Обработка SOCKS5 подключения
            val request = msg
            if (request.type() === Socks5CommandType.CONNECT) {
                // Разрешаем подключение (обязательно для curl)
                ctx.writeAndFlush(
                    DefaultSocks5CommandResponse(
                        Socks5CommandStatus.SUCCESS,
                        request.dstAddrType()
                    )
                )
                return
            }
        } else if (msg is FullHttpRequest) {
            println("TextReplacerHandler : msg is FullHttpRequest")
            ctx.fireChannelRead(msg)
            return

            val req = msg
            var content = req.content().toString(StandardCharsets.UTF_8)


            // Замена текста
            content = content.replace("Футбол", "Баскетбол")


            // Собираем новый запрос
            val newReq: FullHttpRequest = DefaultFullHttpRequest(
                req.protocolVersion(),
                req.method(),
                req.uri(),
                Unpooled.copiedBuffer(content, StandardCharsets.UTF_8)
            )
            newReq.headers().setAll(req.headers())

            ctx.fireChannelRead(newReq)
            return
        } else {
            ctx.fireChannelRead(msg)
        }
        println("TextReplacerHandler : channelRead : end")
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}