package com.github.dima_dencep.mods.online_emotes.netty;

import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;

@ChannelHandler.Sharable
public class HandshakeHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private final WebSocketClientHandshaker handshaker;
    public ChannelPromise handshakeFuture;

    public HandshakeHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);

        this.handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        this.handshaker.handshake(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) {
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ctx.channel(), msg);
            handshakeFuture.setSuccess();
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        } else {
            OnlineEmotes.logger.error("WebSocket exception:", cause);
        }
        ctx.close();
    }
}