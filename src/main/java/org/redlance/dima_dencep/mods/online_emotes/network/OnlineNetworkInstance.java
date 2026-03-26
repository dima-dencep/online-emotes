/*
 * Copyright 2023 - 2026 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.network;

import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.common.network.PacketConfig;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotesPlatform;
import org.redlance.dima_dencep.mods.online_emotes.client.FancyToast;
import org.redlance.dima_dencep.mods.online_emotes.netty.HandshakeHandler;
import org.redlance.dima_dencep.mods.online_emotes.netty.WebsocketHandler;
import org.redlance.dima_dencep.mods.online_emotes.utils.EmotePacketWrapper;
import org.redlance.dima_dencep.mods.online_emotes.utils.NettyObjectFactory;
import io.github.kosmx.emotes.api.proxy.AbstractNetworkInstance;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.concurrent.ScheduledFuture;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redlance.platformtools.referer.PlatformFileReferer;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class OnlineNetworkInstance extends AbstractNetworkInstance {
    private static final URI URI_ADDRESS = URI.create("wss://api.redlance.org:443/websockets/online-emotes");
    public static final int PAYLOAD_LENGTH = Integer.MAX_VALUE;

    public final Bootstrap bootstrap = new Bootstrap();
    private ScheduledFuture<?> reconnectingFuture;
    public HandshakeHandler handshakeHandler;
    public Channel ch;

    public OnlineNetworkInstance() {
        this.bootstrap.group(NettyObjectFactory.newEventLoopGroup());
        this.bootstrap.channel(NettyObjectFactory.getSocketChannel());
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(@NotNull SocketChannel ch) throws SSLException {
                ChannelPipeline pipeline = ch.pipeline();

                if ("wss".equals(URI_ADDRESS.getScheme())) {
                    pipeline.addLast(SslContextBuilder.forClient().build()
                            .newHandler(ch.alloc(), URI_ADDRESS.getHost(), URI_ADDRESS.getPort())
                    );
                }

                pipeline.addLast("http-codec", new HttpClientCodec());
                pipeline.addLast("aggregator", new HttpObjectAggregator(PAYLOAD_LENGTH));
                pipeline.addLast("ws-compression", new WebSocketClientCompressionHandler(PAYLOAD_LENGTH));
                pipeline.addLast("handshaker", OnlineNetworkInstance.this.handshakeHandler);
                pipeline.addLast("ws-handler", new WebsocketHandler(OnlineNetworkInstance.this));
            }
        });
    }

    public void connect() {
        if (isActive()) {
            sendOnlineEmotesConfig();
            return;
        }

        stopReconnecting();

        this.reconnectingFuture = this.bootstrap.config().group().scheduleWithFixedDelay(() -> {
            if (!isActive()) {
                OnlineEmotes.LOGGER.info("Try (re)connecting...");

                ChannelFuture future = connectInternal();
                future.awaitUninterruptibly();

                if (future.isSuccess()) {
                    this.handshakeHandler.handshakeFuture.awaitUninterruptibly();
                }
            }

        }, 0L, OnlineEmotes.getConfig().reconnectionDelay.get(), TimeUnit.SECONDS);
    }

    private ChannelFuture connectInternal() {
        this.handshakeHandler = new HandshakeHandler(WebSocketClientHandshakerFactory.newHandshaker(URI_ADDRESS,
                WebSocketVersion.V13, null, true, createHeaders(), PAYLOAD_LENGTH
        ));

        ChannelFuture channelFuture = this.bootstrap.connect(URI_ADDRESS.getHost(), URI_ADDRESS.getPort());
        channelFuture.addListener((l) -> {
            if (l.isSuccess()) {
                disconnectNetty();

                this.ch = channelFuture.channel();

                this.handshakeHandler.handshakeFuture.addListener((e) -> {
                    if (e.isSuccess()) {
                        sendOnlineEmotesConfig();
                    } else {
                        OnlineEmotes.LOGGER.error("Failed to connect!", e.cause());
                    }
                });
            } else {
                OnlineEmotes.LOGGER.error("Failed to connect!", l.cause());
            }
        });
        return channelFuture;
    }

    @Override
    public boolean sendPlayerID() {
        return true;
    }

    public void sendOnlineEmotesConfig() {
        sendC2SConfig(builder -> {
            try {
                sendMessage(builder, null);
            } catch (IOException e) {
                OnlineEmotes.LOGGER.fatal("Failed to send message!", e);
            }
        });
    }

    @Override
    public boolean isActive() {
        return this.ch != null && this.ch.isActive() &&
                this.handshakeHandler != null && this.handshakeHandler.isSuccess();
    }

    @Override
    public void sendMessage(EmotePacket.Builder builder, @Nullable UUID target) throws IOException {
        if (!isActive()) {
            OnlineEmotes.LOGGER.error("Can't send packet to an inactive channel!");
            return;
        }

        if (target != null) {
            builder.configureTarget(target);
        }

        EmotePacket writer = builder.setSizeLimit(PAYLOAD_LENGTH, false).build();
        this.ch.writeAndFlush(new EmotePacketWrapper(writer).toWebSocketFrame(this.ch.alloc()), this.ch.voidPromise());

        if (writer.data.emoteData != null && writer.data.emoteData.data().has("song") && writer.data.skippedPackets.contains(PacketConfig.NBS_CONFIG)) {
            FancyToast.sendMessage(Component.translatable("emotecraft.song_too_big_to_send"));
        }
    }

    protected void disconnectNetty() {
        Channel channel = this.ch;
        this.ch = null;

        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new CloseWebSocketFrame())
                    .addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void disconnect() {
        stopReconnecting();
        disconnectNetty();
        super.disconnect();
    }

    private void stopReconnecting() {
        try {
            if (this.reconnectingFuture != null) {
                this.reconnectingFuture.cancel(true);
                this.reconnectingFuture = null;
            }
        } catch (Throwable th) {
            OnlineEmotes.LOGGER.error("Failed to stop reconnector:", th);
        }
    }

    @SuppressWarnings("unused")
    public boolean isReconnectorAlive() {
        return this.reconnectingFuture != null;
    }

    private static HttpHeaders createHeaders() {
        DefaultHttpHeaders headers = new DefaultHttpHeaders();

        headers.add(HttpHeaderNames.USER_AGENT, String.format("%s/%s %s/%s Minecraft/%s",
                OnlineEmotes.MOD_ID, OnlineEmotesPlatform.INSTANCE.getModVersion(OnlineEmotes.MOD_ID),
                CommonData.MOD_NAME, OnlineEmotesPlatform.INSTANCE.getModVersion(CommonData.MOD_ID),
                SharedConstants.getProtocolVersion()
        ));

        try {
            for (String referer : PlatformFileReferer.INSTANCE.getFileReferer(OnlineEmotesPlatform.INSTANCE.getModFile(OnlineEmotes.MOD_ID))) {
                headers.add(HttpHeaderNames.REFERER, URLEncoder.encode(referer, StandardCharsets.UTF_8));
            }
        } catch (Throwable th) {
            headers.add(HttpHeaderNames.REFERER, URLEncoder.encode(th.toString(), StandardCharsets.UTF_8));
        }

        try { // Because LanguageManager is reloadable
            headers.add(HttpHeaderNames.ACCEPT_LANGUAGE, Minecraft.getInstance().getLanguageManager().getSelected());
        } catch (Throwable ignored) {}

        OnlineEmotes.LOGGER.info("Headers: {}", headers.unwrap());
        return headers;
    }
}
