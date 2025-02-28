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

import io.netty.channel.epoll.Epoll;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.redlance.dima_dencep.mods.online_emotes.network.OnlineNetworkInstance;

public class OnlineEmotesConfig {
    public static final Pair<OnlineEmotesConfig, ModConfigSpec> CONFIG_SPEC_PAIR = new ModConfigSpec.Builder()
            .configure(OnlineEmotesConfig::new);

    // Global
    public final ModConfigSpec.ConfigValue<Long> reconnectionDelay;
    public final ModConfigSpec.BooleanValue replaceMessages;
    public final ModConfigSpec.BooleanValue debug;

    // Netty
    public final ModConfigSpec.BooleanValue useEpoll;
    public final ModConfigSpec.IntValue compressionThreshold;

    public OnlineEmotesConfig(ModConfigSpec.Builder builder) {
        builder.push("global");
        this.reconnectionDelay = builder.defineInRange("reconnectionDelay", 15L, 0L, Long.MAX_VALUE);
        this.replaceMessages = builder.define("replaceMessages", false);
        this.debug = builder.define("debug", false);
        builder.pop();

        builder.push("netty");
        this.useEpoll = builder.define("useEpoll", Epoll.isAvailable());
        this.compressionThreshold = builder.defineInRange("compressionThreshold",
                256, 256, OnlineNetworkInstance.PAYLOAD_LENGHT
        );
        builder.pop();
    }

    public static long reconnectionDelay() {
        return OnlineEmotesConfig.CONFIG_SPEC_PAIR.getKey().reconnectionDelay.get();
    }

    public static boolean replaceMessages() {
        return OnlineEmotesConfig.CONFIG_SPEC_PAIR.getKey().replaceMessages.get();
    }

    public static boolean debug() {
        return OnlineEmotesConfig.CONFIG_SPEC_PAIR.getKey().debug.get();
    }

    public static boolean useEpoll() {
        return OnlineEmotesConfig.CONFIG_SPEC_PAIR.getKey().useEpoll.get();
    }

    public static int compressionThreshold() {
        return OnlineEmotesConfig.CONFIG_SPEC_PAIR.getKey().compressionThreshold.get();
    }
}
