package com.github.dima_dencep.mods.online_emotes.utils;

import io.github.kosmx.emotes.main.config.ClientSerializer;
import io.github.kosmx.emotes.server.config.Serializer;
import io.netty.buffer.Unpooled;
import io.netty.channel.local.LocalAddress;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class EmotePacketWrapper {
    public final byte[] emotePacket;

    @Nullable
    public String playerName;

    @Nullable
    public UUID playerUUID;

    @Nullable
    public String serverAddress;

    public EmotePacketWrapper(byte[] emotePacket) {
        this.emotePacket = emotePacket;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            this.playerName = player.getEntityName();
            this.playerUUID = player.getUuid();

            if (player.networkHandler != null) {
                this.serverAddress = getIP(player.networkHandler.connection.getAddress());
            }
        }

        if (Serializer.serializer == null) {
            new ClientSerializer().initializeSerializer();
        }
    }

    public BinaryWebSocketFrame toWebSocketFrame() {
        return new BinaryWebSocketFrame(Unpooled.wrappedBuffer(Serializer.serializer.toJson(this).getBytes(StandardCharsets.UTF_8)));
    }

    private static String getIP(SocketAddress address) {
        if (address instanceof LocalAddress) {
            return null;

        } else if (address instanceof InetSocketAddress inetSocketAddress) {
            return inetSocketAddress.getAddress().getHostAddress();
        }

        return address.toString();
    }
}
