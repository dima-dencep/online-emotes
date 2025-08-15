/*
 * Copyright 2023 - 2025 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.utils;

import com.mojang.authlib.GameProfile;
import io.github.kosmx.emotes.api.proxy.INetworkInstance;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.ByteBufCodecs;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;

public class EmotePacketWrapper {
    public final byte[] emotePacket;
    public GameProfile gameProfile;

    @Nullable
    public UUID playerWorldId;
    @Nullable
    public String serverAddress;

    public EmotePacketWrapper(EmotePacket packet) throws IOException {
        this(INetworkInstance.safeGetBytesFromBuffer(packet.write()));
    }

    public EmotePacketWrapper(byte[] emotePacket) {
        this.emotePacket = emotePacket;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            this.gameProfile = player.getGameProfile();
            this.playerWorldId = player.getUUID();

            Connection connection = player.connection.getConnection();
            if (!connection.isMemoryConnection()) {
                this.serverAddress = getIP(connection.getRemoteAddress());
            }
        }
    }

    public WebSocketFrame toWebSocketFrame() {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeByte(0); // Version
        ByteBufCodecs.GAME_PROFILE.encode(byteBuf, this.gameProfile); // Profile

        // Level data
        FriendlyByteBuf.writeNullable(byteBuf, this.playerWorldId, FriendlyByteBuf::writeUUID);
        FriendlyByteBuf.writeNullable(byteBuf, this.serverAddress,
                (buf, address) -> Utf8String.write(buf, address, 32767)
        );

        FriendlyByteBuf.writeByteArray(byteBuf, this.emotePacket); // Emote Packet
        return new BinaryWebSocketFrame(byteBuf); // Frame
    }

    private static String getIP(SocketAddress address) {
        if (address instanceof InetSocketAddress inetSocketAddress) {
            return inetSocketAddress.getAddress().getHostAddress();
        }

        return address.toString();
    }
}
