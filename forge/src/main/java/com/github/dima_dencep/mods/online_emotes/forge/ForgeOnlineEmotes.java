/*
 * Copyright 2023 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package com.github.dima_dencep.mods.online_emotes.forge;

import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod(OnlineEmotes.MOD_ID)
public class ForgeOnlineEmotes extends OnlineEmotes {
    public ForgeOnlineEmotes() {
        NeoForge.EVENT_BUS.register(this);

        super.onInitializeClient();
    }

    @SubscribeEvent
    public void onJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (proxy.isActive()) {
            proxy.sendOnlineEmotesConfig();
        } else {
            proxy.connect();
        }
    }

    @SubscribeEvent
    public void onExit(ClientPlayerNetworkEvent.LoggingOut event) {
        if (proxy.isActive()) {
            proxy.disconnect();
        }
    }
}
