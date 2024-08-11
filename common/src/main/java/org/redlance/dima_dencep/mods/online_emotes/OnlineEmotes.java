/*
 * Copyright 2023 - 2024 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes;

import org.redlance.dima_dencep.mods.online_emotes.network.OnlineNetworkInstance;
import io.github.kosmx.emotes.api.proxy.EmotesProxyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OnlineEmotes {
    public static final Logger LOGGER = LogManager.getLogger(OnlineEmotes.MOD_ID);
    public static final String MOD_ID = "online_emotes";
    public static OnlineNetworkInstance proxy;

    public void onInitializeClient() {
        EmotesProxyManager.registerProxyInstance(OnlineEmotes.proxy = new OnlineNetworkInstance());

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                OnlineEmotes.proxy.bootstrap.config().group().shutdownGracefully()
        ));
    }
}
