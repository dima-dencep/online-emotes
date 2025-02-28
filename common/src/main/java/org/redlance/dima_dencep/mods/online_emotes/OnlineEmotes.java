/*
 * Copyright 2023 - 2025 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import org.redlance.dima_dencep.mods.online_emotes.network.OnlineNetworkInstance;
import io.github.kosmx.emotes.api.proxy.EmotesProxyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class OnlineEmotes {
    public static final Logger LOGGER = LogManager.getLogger(OnlineEmotes.MOD_ID);
    public static final String MOD_ID = "online_emotes";
    public static OnlineNetworkInstance proxy;

    protected void onInitializeClient() {
        EmotesProxyManager.registerProxyInstance(OnlineEmotes.proxy = new OnlineNetworkInstance());
    }

    protected void onLoggingIn(LocalPlayer player, Connection connection) {
        if (proxy.isActive()) {
            proxy.sendOnlineEmotesConfig();
        } else {
            proxy.connect();
        }
    }

    protected void onLoggingOut(LocalPlayer player, Connection connection) {
        if (proxy.isActive()) {
            proxy.disconnect();
        }
    }
}
