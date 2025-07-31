package server2

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpHeaderNames
import java.nio.charset.StandardCharsets

class HttpResponseModifier : SimpleChannelInboundHandler<FullHttpResponse>() {
    protected override fun channelRead0(ctx: ChannelHandlerContext, response: FullHttpResponse) {
        println("SocksServer : HttpResponseModifier channelRead0 : ${response}")
        // 1. Получаем контент
        val content: ByteBuf = response.content()
        var text: String = content.toString(StandardCharsets.UTF_8)

        // 2. Модифицируем (замена слова)
        text = text.replace("Футбол", "Баскетбол")

        // 3. Создаём новый буфер с изменёнными данными
        val newContent: ByteBuf = Unpooled.copiedBuffer(text, StandardCharsets.UTF_8)

        // 4. Собираем новый ответ
        val modifiedResponse: FullHttpResponse = DefaultFullHttpResponse(
            response.protocolVersion(),
            response.status(),
            newContent
        )
        modifiedResponse.headers().setAll(response.headers())
        modifiedResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, newContent.readableBytes())

        // 5. Отправляем клиенту
        ctx.writeAndFlush(modifiedResponse)

        // 6. Освобождаем оригинальный буфер
        response.release()
    }
}