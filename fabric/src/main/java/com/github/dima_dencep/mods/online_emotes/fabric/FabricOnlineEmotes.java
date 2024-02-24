/*
 * Copyright 2023 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package com.github.dima_dencep.mods.online_emotes.fabric;

import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;


public class FabricOnlineEmotes extends OnlineEmotes implements ClientModInitializer {
    public static final ConfigExpectPlatformImpl MOD_CONFIG = AutoConfig.register(
            ConfigExpectPlatformImpl.class, Toml4jConfigSerializer::new
    ).get();

    @Override
    public void onInitializeClient() {
        super.onInitializeClient();

        ClientPlayConnectionEvents.JOIN.register(this::onJoin);
        ClientPlayConnectionEvents.DISCONNECT.register(this::onDisconnect);
    }

    public void onJoin(ClientPacketListener handler, PacketSender packetSender, Minecraft minecraftClient) {
        if (proxy.isActive()) {
            proxy.sendOnlineEmotesConfig();
        } else {
            proxy.connect();
        }
    }

    private void onDisconnect(ClientPacketListener handler, Minecraft minecraftClient) {
        if (proxy.isActive()) {
            proxy.disconnect();
        }
    }
}
