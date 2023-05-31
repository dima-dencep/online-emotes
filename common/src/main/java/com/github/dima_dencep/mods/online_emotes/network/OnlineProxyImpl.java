package com.github.dima_dencep.mods.online_emotes.network;

import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import com.github.dima_dencep.mods.online_emotes.netty.HandshakeHandler;
import com.github.dima_dencep.mods.online_emotes.netty.WebsocketHandler;
import com.github.dima_dencep.mods.online_emotes.utils.NettyObjectFactory;
import io.github.kosmx.emotes.api.proxy.AbstractNetworkInstance;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.executor.EmoteInstance;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContextBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@ChannelHandler.Sharable
public class OnlineProxyImpl extends AbstractNetworkInstance {
    public final URI uri = URI.create(OnlineEmotes.config.address);
    public final AtomicBoolean block = new AtomicBoolean();
    public final Bootstrap bootstrap = new Bootstrap();
    public HandshakeHandler handshakeHandler;
    public Channel ch;

    public OnlineProxyImpl() {
        String protocol = this.uri.getScheme();
        if (!"ws".equals(protocol) && !"wss".equals(protocol)) {
            throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }

        EventLoopGroup loopGroup = NettyObjectFactory.newEventLoopGroup();
        loopGroup.scheduleAtFixedRate(() -> {
            if (!block.get()) connectAsync();
        }, 0L, OnlineEmotes.config.reconnectionDelay, TimeUnit.SECONDS);

        this.bootstrap.group(loopGroup);
        this.bootstrap.channel(NettyObjectFactory.getSocketChannel());
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(@NotNull SocketChannel ch) throws SSLException {
                ChannelPipeline pipeline = ch.pipeline();

                if ("wss".equals(protocol)) {
                    pipeline.addLast(SslContextBuilder.forClient().build().newHandler(ch.alloc(), uri.getHost(), uri.getPort()));
                }

                pipeline.addLast("http-codec", new HttpClientCodec());
                pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                pipeline.addLast("handshaker", OnlineProxyImpl.this.handshakeHandler);
                pipeline.addLast("ws-handler", new WebsocketHandler(OnlineProxyImpl.this));
            }
        });
    }

    public void connectAsync() {
        if (this.isActive() || this.block.get()) {
            throw new IllegalStateException("Already connected!");
        }

        OnlineEmotes.logger.info("Preparing a new connection...");
        this.block.set(true);

        this.handshakeHandler = new HandshakeHandler(WebSocketClientHandshakerFactory.newHandshaker(
                this.uri,
                WebSocketVersion.V13,
                null,
                false,
                EmptyHttpHeaders.INSTANCE,
                12800000)
        );

        ChannelFuture channelFuture = this.bootstrap.connect(this.uri.getHost(), this.uri.getPort());
        channelFuture.addListener((l) -> {
            if (l.isSuccess()) {
                this.ch = channelFuture.channel();

                this.handshakeHandler.handshakeFuture.addListener((e) -> {
                    if (e.isSuccess()) {
                        this.block.set(true);
                        sendConfigCallback();
                    } else {
                        OnlineEmotes.logger.error(e.cause());
                        this.block.set(false);
                    }
                });
            } else {
                OnlineEmotes.logger.error(l.cause());
                this.block.set(false);
            }
        });
    }

    @Override
    public boolean sendPlayerID() {
        return true;
    }

    @Override
    public boolean isActive() {
        return this.ch != null && this.ch.isActive();
    }

    @Override
    public void sendMessage(EmotePacket.Builder builder, @Nullable UUID target) throws IOException {
        if (target != null) {
            builder.configureTarget(target);
        }

        EmotePacket writer = builder.build();

        this.ch.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(writer.write().array())), this.ch.voidPromise());

        if (writer.data.emoteData != null && writer.data.emoteData.extraData.containsKey("song") && !writer.data.writeSong) {
            EmoteInstance.instance.getClientMethods().sendChatMessage(EmoteInstance.instance.getDefaults().newTranslationText("emotecraft.song_too_big_to_send"));
        }
    }

    @Override
    public void disconnect() {
        if (this.isActive()) {
            this.ch.writeAndFlush(new CloseWebSocketFrame(), this.ch.voidPromise());
            try {
                this.ch.closeFuture().sync();
            } catch (InterruptedException ignored) {
            }
            this.block.set(false);
        }

        super.disconnect();
    }

    @Override
    public void sendConfigCallback() {
        EmotePacket.Builder packetBuilder = new EmotePacket.Builder();
        packetBuilder.configureToConfigExchange(true);

        if (OnlineEmotes.client.player != null) packetBuilder.configureTarget(OnlineEmotes.client.player.getUuid());

        try {
            this.sendMessage(packetBuilder, null);
        } catch (Exception var3) {
            OnlineEmotes.logger.warn("Error while writing packet:", var3);
        }
    }
}
