/*
 * Copyright 2023 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package com.github.dima_dencep.mods.online_emotes.utils;

import io.github.kosmx.emotes.main.config.ClientSerializer;
import io.github.kosmx.emotes.server.config.Serializer;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
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
    public boolean localizedMsg;

    public EmotePacketWrapper(byte[] emotePacket) {
        this.emotePacket = emotePacket;
        this.localizedMsg = true;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            this.playerName = player.getScoreboardName();
            this.playerUUID = player.getUUID();

            Connection connection = player.connection.getConnection();
            if (!connection.isMemoryConnection()) {
                this.serverAddress = getIP(connection.getRemoteAddress());
            }
        }

        if (Serializer.serializer == null) {
            new ClientSerializer().initializeSerializer();
        }
    }

    public BinaryWebSocketFrame toWebSocketFrame() {
        return new BinaryWebSocketFrame(Unpooled.wrappedBuffer(Serializer.serializer.toJson(this)
                .getBytes(StandardCharsets.UTF_8)
        ));
    }

    private static String getIP(SocketAddress address) {
        if (address instanceof InetSocketAddress inetSocketAddress) {
            return inetSocketAddress.getAddress().getHostAddress();
        }

        return address.toString();
    }
}
