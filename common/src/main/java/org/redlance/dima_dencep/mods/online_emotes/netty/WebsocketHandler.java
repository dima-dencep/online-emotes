/*
 * Copyright 2023 - 2025 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.netty;

import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotesConfig;
import org.redlance.dima_dencep.mods.online_emotes.client.FancyToast;
import org.redlance.dima_dencep.mods.online_emotes.network.OnlineNetworkInstance;
import io.github.kosmx.emotes.PlatformTools;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

@ChannelHandler.Sharable
public class WebsocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private static final Component DISCONNECTED = Component.translatable("online_emotes.messages.disconnected");
    private static final Component CONNECTED = Component.translatable("online_emotes.messages.connected");

    private final OnlineNetworkInstance proxy;

    public WebsocketHandler(OnlineNetworkInstance proxy) {
        this.proxy = proxy;
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        if (OnlineEmotesConfig.debug()) {
            FancyToast.sendMessage(WebsocketHandler.DISCONNECTED);
        }
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        if (OnlineEmotesConfig.debug()) {
            FancyToast.sendMessage(WebsocketHandler.CONNECTED);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
        switch (msg) {
            case BinaryWebSocketFrame frame -> {
                ByteBuf buf = frame.content();

                if (!buf.isDirect() && !buf.isReadOnly()) {
                    this.proxy.receiveMessage(buf.array());
                } else {
                    byte[] bytes = new byte[buf.readableBytes()];
                    buf.getBytes(buf.readerIndex(), bytes);
                    this.proxy.receiveMessage(bytes);
                }
            }

            case TextWebSocketFrame frame -> FancyToast.sendMessage(PlatformTools.fromJson(frame.text()));

            case PingWebSocketFrame frame -> {
                frame.content().retain();
                ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content()), ctx.channel().voidPromise());
            }

            case CloseWebSocketFrame ignored -> ctx.channel().close();

            default -> OnlineEmotes.LOGGER.error("Unsupported frame type: {}!", msg.getClass().getName());
        }
    }
}