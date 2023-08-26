package com.github.dima_dencep.mods.online_emotes.netty;

import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import com.github.dima_dencep.mods.online_emotes.network.OnlineNetworkInstance;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import org.jetbrains.annotations.NotNull;

@ChannelHandler.Sharable
public class WebsocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final OnlineNetworkInstance proxy;

    public WebsocketHandler(OnlineNetworkInstance proxy) {
        this.proxy = proxy;
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        OnlineEmotes.sendMessage(true, null, "WebSocket disconnected!");
        OnlineEmotes.LOGGER.info("WebSocket disconnected!");
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        OnlineEmotes.sendMessage(true, null, "WebSocket connected!");
        OnlineEmotes.LOGGER.info("WebSocket connected!");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
        if (msg instanceof BinaryWebSocketFrame frame) {
            ByteBuf buf = frame.content();

            if (!buf.isDirect() && !buf.isReadOnly()) {
                this.proxy.receiveMessage(buf.array());
            } else {
                byte[] bytes = new byte[buf.readableBytes()];
                buf.getBytes(buf.readerIndex(), bytes);
                this.proxy.receiveMessage(bytes);
            }

        } else if (msg instanceof TextWebSocketFrame frame) {
            OnlineEmotes.sendMessage(false, null, frame.text());

        } else if (msg instanceof PingWebSocketFrame frame) {
            frame.content().retain();
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content()), ctx.channel().voidPromise());

        } else if (msg instanceof CloseWebSocketFrame) {
            ctx.channel().close();

        } else {
            OnlineEmotes.LOGGER.error("Unsupported frame type: %s!", msg.getClass().getName());
        }
    }
}