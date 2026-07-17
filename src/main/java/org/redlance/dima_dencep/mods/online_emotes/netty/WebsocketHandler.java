/*
 * Copyright 2023 - 2026 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.netty;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zigythebird.playeranimcore.PlayerAnimLib;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.common.network.PacketBound;
import io.github.kosmx.emotes.mc.McUtils;
import net.minecraft.core.RegistryAccess;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;
import org.redlance.dima_dencep.mods.online_emotes.client.FancyToast;
import org.redlance.dima_dencep.mods.online_emotes.network.OnlineNetworkInstance;
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

        if (OnlineEmotes.getConfig().debug.get()) {
            FancyToast.sendMessage(WebsocketHandler.DISCONNECTED);
        }
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        if (OnlineEmotes.getConfig().debug.get()) {
            FancyToast.sendMessage(WebsocketHandler.CONNECTED);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
        switch (msg) {
            case BinaryWebSocketFrame frame -> this.proxy.receiveMessage(new EmotePacket(frame.content(), PacketBound.CLIENT), null);

            case TextWebSocketFrame frame -> {
                try {
                    JsonElement element = PlayerAnimLib.GSON.fromJson(frame.text(), JsonElement.class);
                    if (!element.isJsonObject()) {
                        FancyToast.sendMessage(McUtils.fromJson(element, RegistryAccess.EMPTY));
                        break;
                    }

                    JsonObject object = element.getAsJsonObject();
                    if (object.has("message")) {
                        FancyToast.sendMessage(McUtils.fromJson(object.get("message"), RegistryAccess.EMPTY));
                    }

                    // TODO
                } catch (Exception e) {
                    OnlineEmotes.LOGGER.error("Failed to parse text frame: {}", frame.text(), e);
                }
            }

            case PingWebSocketFrame frame -> {
                frame.content().retain();
                ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content()), ctx.channel().voidPromise());
            }

            case CloseWebSocketFrame ignored -> ctx.channel().close();

            default -> OnlineEmotes.LOGGER.error("Unsupported frame type: {}!", msg.getClass().getName());
        }
    }
}