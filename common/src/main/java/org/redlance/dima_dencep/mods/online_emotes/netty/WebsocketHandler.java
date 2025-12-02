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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.velocitypowered.natives.compression.VelocityCompressor;
import com.velocitypowered.natives.util.Natives;
import com.zigythebird.playeranimcore.PlayerAnimLib;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.mc.McUtils;
import net.minecraft.core.RegistryAccess;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotesConfig;
import org.redlance.dima_dencep.mods.online_emotes.client.FancyToast;
import org.redlance.dima_dencep.mods.online_emotes.netty.compression.VelocityCompressDecoder;
import org.redlance.dima_dencep.mods.online_emotes.netty.compression.VelocityCompressEncoder;
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

    private static final Component COMPRESSIONOFF = Component.translatable("online_emotes.messages.compressionoff");

    private final OnlineNetworkInstance proxy;

    public WebsocketHandler(OnlineNetworkInstance proxy) {
        this.proxy = proxy;
    }

    static {
        OnlineEmotes.LOGGER.info("Compression will use {}", Natives.compress.getLoadedVariant());
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
            case BinaryWebSocketFrame frame -> this.proxy.receiveMessage(new EmotePacket(frame.content()));

            case TextWebSocketFrame frame -> {
                JsonElement element = PlayerAnimLib.GSON.fromJson(frame.text(), JsonElement.class);
                if (!element.isJsonObject()) {
                    FancyToast.sendMessage(McUtils.fromJson(element, RegistryAccess.EMPTY));
                    break;
                }

                JsonObject object = element.getAsJsonObject();
                if (object.has("message")) {
                    FancyToast.sendMessage(McUtils.fromJson(object.get("message"), RegistryAccess.EMPTY));
                }

                if (object.has("compression")) {
                    int compressionLevel = object.get("compression").getAsInt();

                    if (compressionLevel <= 0) {
                        if (ctx.pipeline().get(VelocityCompressDecoder.NAME) instanceof VelocityCompressDecoder) {
                            ctx.pipeline().remove(VelocityCompressDecoder.NAME);
                        }

                        if (ctx.pipeline().get(VelocityCompressEncoder.NAME) instanceof VelocityCompressEncoder) {
                            ctx.pipeline().remove(VelocityCompressEncoder.NAME);
                        }

                        if (OnlineEmotesConfig.debug()) {
                            FancyToast.sendMessage(WebsocketHandler.COMPRESSIONOFF);
                        }
                    } else {
                        VelocityCompressor compressor = Natives.compress.get().create(compressionLevel);

                        if (ctx.pipeline().get(VelocityCompressDecoder.NAME) instanceof VelocityCompressDecoder decoder) {
                            ctx.pipeline().replace(decoder, VelocityCompressDecoder.NAME, new VelocityCompressDecoder(compressor));
                        } else {
                            ctx.pipeline().addAfter("ws-decoder", VelocityCompressDecoder.NAME, new VelocityCompressDecoder(compressor));
                        }

                        if (ctx.pipeline().get(VelocityCompressEncoder.NAME) instanceof VelocityCompressEncoder encoder) {
                            ctx.pipeline().replace(encoder, VelocityCompressEncoder.NAME, new VelocityCompressEncoder(compressor));
                        } else {
                            ctx.pipeline().addAfter("ws-encoder", VelocityCompressEncoder.NAME, new VelocityCompressEncoder(compressor));
                        }

                        if (OnlineEmotesConfig.debug()) {
                            FancyToast.sendMessage(Component.translatable("online_emotes.messages.compressionset", compressionLevel));
                        }
                    }
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