/*
 * Copyright 2023 - 2026 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes;

import io.github.kosmx.emotes.arch.gui.screen.ConfigScreen;
import io.github.kosmx.emotes.server.config.ConfigSerializer;
import io.github.kosmx.emotes.server.config.Serializer;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.kqueue.KQueue;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import org.redlance.dima_dencep.mods.online_emotes.client.FancyToast;
import org.redlance.dima_dencep.mods.online_emotes.network.OnlineNetworkInstance;
import io.github.kosmx.emotes.api.proxy.EmotesProxyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public abstract class OnlineEmotes {
    public static final Logger LOGGER = LogManager.getLogger(OnlineEmotes.MOD_ID);
    public static final String MOD_ID = "online_emotes";
    protected static final Serializer<OnlineEmotesConfig> CONFIG_SERIALIZER = new Serializer<>(
            new ConfigSerializer<>(OnlineEmotesConfig::new, 0),
            OnlineEmotesConfig.class,
            OnlineEmotesPlatform.INSTANCE.getConfigPath()
    );

    public static volatile OnlineNetworkInstance proxy;

    protected void onInitializeClient() {
        EmotesProxyManager.registerProxyInstance(OnlineEmotes.proxy = new OnlineNetworkInstance());

        OnlineEmotes.LOGGER.info("KQueue.isAvailable: {}", KQueue.isAvailable());
        OnlineEmotes.LOGGER.info("Epoll.isAvailable: {}", Epoll.isAvailable());
    }

    protected void onLoggingIn(LocalPlayer player, Connection connection) {
        if (proxy.isActive()) {
            proxy.sendOnlineEmotesConfig();
        } else {
            proxy.connect();
        }
    }

    protected void onLoggingOut(LocalPlayer player, Connection connection) {
        proxy.disconnect();
    }

    public static Screen createConfigScreen(Screen parent) {
        return new ConfigScreen(parent, getConfig(), OnlineEmotes.MOD_ID, FancyToast.TITLE);
    }

    public static OnlineEmotesConfig getConfig() {
        return OnlineEmotes.CONFIG_SERIALIZER.readConfig(false);
    }
}
