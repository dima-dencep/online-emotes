package com.github.dima_dencep.mods.online_emotes.fabric;

import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class FabricOnlineEmotes extends OnlineEmotes implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        super.onInitializeClient();

        ClientPlayConnectionEvents.JOIN.register(this::onJoin);
        ClientPlayConnectionEvents.DISCONNECT.register(this::onDisconnect);
    }

    public void onJoin(ClientPlayNetworkHandler handler, PacketSender packetSender, MinecraftClient minecraftClient) {
        if (proxy.isActive()) {
            proxy.sendConfigCallback();
        } else {
            proxy.connect();
        }
    }

    private void onDisconnect(ClientPlayNetworkHandler handler, MinecraftClient minecraftClient) {
        if (proxy.isActive()) {
            proxy.disconnect();
        }
    }
}
