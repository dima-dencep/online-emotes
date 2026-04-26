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

import io.github.kosmx.emotes.common.SerializableConfig;

public final class OnlineEmotesConfig extends SerializableConfig {
    // Global
    public final ConfigEntry<Long> reconnectionDelay = new NumberConfigEntry<>("reconnectionDelay", 15L, true, category("global"), 0L, 300L) {
        @Override
        public Long fromDouble(double value) {
            return (long) value;
        }
    };
    public final ConfigEntry<Boolean> replaceMessages = new ConfigEntry<>("replaceMessages", false, false, category("global"));
    public final ConfigEntry<Boolean> debug = new ConfigEntry<>("debug", false, false, category("global"));

    // Netty
    // public final ModConfigSpec.BooleanValue useEpoll;
    // public final ModConfigSpec.BooleanValue useKQueue;

    public OnlineEmotesConfig() {
        /*builder.push("netty");
        this.useEpoll = builder.define("useEpoll", true);
        this.useKQueue = builder.define("useKQueue", true);
        builder.pop();*/
    }

    /*public static boolean useEpoll() {
        return OnlineEmotesConfig.CONFIG_SPEC_PAIR.getKey().useEpoll.get();
    }

    public static boolean useKQueue() {
        return OnlineEmotesConfig.CONFIG_SPEC_PAIR.getKey().useKQueue.get();
    }*/
}
