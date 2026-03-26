/*
 * Copyright 2023 - 2026 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.fabric;

import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class FabricOnlineEmotes extends OnlineEmotes implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        super.onInitializeClient();

        ClientPlayConnectionEvents.JOIN.register((handler, _, client) ->
                onLoggingIn(client.player, handler.getConnection())
        );
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
                onLoggingOut(client.player, handler.getConnection())
        );
    }
}
