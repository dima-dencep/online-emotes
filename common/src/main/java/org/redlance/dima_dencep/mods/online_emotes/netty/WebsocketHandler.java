/*
 * Copyright 2023 - 2024 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.netty;

import org.redlance.dima_dencep.mods.online_emotes.ConfigExpectPlatform;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;
import org.redlance.dima_dencep.mods.online_emotes.client.FancyToast;
import org.redlance.dima_dencep.mods.online_emotes.network.OnlineNetworkInstance;
import io.github.kosmx.emotes.PlatformTools;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.concurrent.ScheduledFuture;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class WebsocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private static final Component DISCONNECTED = Component.translatable("online_emotes.messages.disconnected");
    private static final Component CONNECTED = Component.translatable("online_emotes.messages.connected");

    private final OnlineNetworkInstance proxy;
    private ScheduledFuture<?> future;

    public WebsocketHandler(OnlineNetworkInstance proxy) {
        this.proxy = proxy;
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        if (ConfigExpectPlatform.debug()) {
            FancyToast.sendMessage(DISCONNECTED);
        }

        if (future != null) {
            future.cancel(true);
        }
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        if (ConfigExpectPlatform.debug()) {
            FancyToast.sendMessage(CONNECTED);
        }

        try {
            if (ConfigExpectPlatform.selfPings())
                this.future = ctx.executor().scheduleWithFixedDelay(
                        () -> ctx.channel().writeAndFlush(new PingWebSocketFrame()),
                        20L, 20L, TimeUnit.SECONDS
                );
        } catch (Throwable th) {
            OnlineEmotes.LOGGER.warn("Failed to schedule server ping!", th);
        }
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
            FancyToast.sendMessage(PlatformTools.fromJson(frame.text()));

        } else if (msg instanceof PingWebSocketFrame frame) {
            frame.content().retain();
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content()), ctx.channel().voidPromise());

        } else if (msg instanceof CloseWebSocketFrame) {
            ctx.channel().close();

        } else {
            OnlineEmotes.LOGGER.error("Unsupported frame type: {}!", msg.getClass().getName());
        }
    }
}