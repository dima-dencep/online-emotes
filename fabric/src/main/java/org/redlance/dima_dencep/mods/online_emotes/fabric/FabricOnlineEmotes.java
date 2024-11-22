/*
 * Copyright 2023 - 2024 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.fabric;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotesConfig;

public class FabricOnlineEmotes extends OnlineEmotes implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NeoForgeConfigRegistry.INSTANCE.register(OnlineEmotes.MOD_ID, ModConfig.Type.CLIENT,
                OnlineEmotesConfig.CONFIG_SPEC_PAIR.getValue(), "online_emotes.toml"
        );
        ConfigScreenFactoryRegistry.INSTANCE.register(OnlineEmotes.MOD_ID, ConfigurationScreen::new);

        super.onInitializeClient();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
                onLoggingIn(client.player, handler.getConnection())
        );
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
                onLoggingOut(client.player, handler.getConnection())
        );
    }
}
