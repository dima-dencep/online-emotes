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

import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;
import io.netty.channel.epoll.Epoll;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@SuppressWarnings("unused")
@Config(name = OnlineEmotes.MOD_ID)
public class ConfigExpectPlatformImpl implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public long reconnectionDelay = 15L;

    public boolean replaceMessages = false;

    public boolean debug = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("netty")
    @ConfigEntry.Gui.RequiresRestart
    public boolean useEpoll = Epoll.isAvailable();

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("netty")
    @ConfigEntry.Gui.RequiresRestart
    @ConfigEntry.BoundedDiscrete(min = 0, max = Integer.MAX_VALUE)
    public int threads = 0;

    public static long reconnectionDelay() {
        return FabricOnlineEmotes.MOD_CONFIG.reconnectionDelay;
    }

    public static boolean replaceMessages() {
        return FabricOnlineEmotes.MOD_CONFIG.replaceMessages;
    }

    public static boolean debug() {
        return FabricOnlineEmotes.MOD_CONFIG.debug;
    }

    public static boolean useEpoll() {
        return FabricOnlineEmotes.MOD_CONFIG.useEpoll;
    }

    public static int threads() {
        return FabricOnlineEmotes.MOD_CONFIG.threads;
    }
}
