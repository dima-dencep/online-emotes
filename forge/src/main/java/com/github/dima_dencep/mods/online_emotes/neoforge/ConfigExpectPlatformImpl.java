/*
 * Copyright 2023 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package com.github.dima_dencep.mods.online_emotes.neoforge;

import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import io.netty.channel.epoll.Epoll;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ConfigFileTypeHandler;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.net.URI;

@SuppressWarnings("unused")
public class ConfigExpectPlatformImpl {
    public static final Pair<ConfigExpectPlatformImpl, ModConfigSpec> CONFIG_SPEC_PAIR = new ModConfigSpec.Builder()
            .configure(ConfigExpectPlatformImpl::new);
    public final ModConfigSpec.ConfigValue<Long> reconnectionDelay;
    public final ModConfigSpec.BooleanValue replaceMessages;
    public final ModConfigSpec.BooleanValue debug;
    public final ModConfigSpec.ConfigValue<String> address;
    public final ModConfigSpec.IntValue maxContentLength;
    public final ModConfigSpec.BooleanValue useEpoll;
    public final ModConfigSpec.BooleanValue selfPings;
    public final ModConfigSpec.ConfigValue<Integer> threads;

    public ConfigExpectPlatformImpl(ModConfigSpec.Builder builder) {
        reconnectionDelay = builder
                .translation("text.autoconfig.online_emotes.option.reconnectionDelay")
                .comment("text.autoconfig.online_emotes.option.reconnectionDelay.@Tooltip")
                .define("reconnectionDelay", 15L);

        replaceMessages = builder
                .translation("text.autoconfig.online_emotes.option.replaceMessages")
                .define("replaceMessages", true);

        debug = builder
                .translation("text.autoconfig.online_emotes.option.debug")
                .define("debug", false);

        address = builder
                .translation("text.autoconfig.online_emotes.option.address")
                .worldRestart()
                .define("address", "wss://api.constructlegacy.ru:443/websockets/online-emotes");

        maxContentLength = builder
                .translation("text.autoconfig.online_emotes.option.maxContentLength")
                .comment("text.autoconfig.online_emotes.option.maxContentLength.@Tooltip")
                .worldRestart()
                .defineInRange("maxContentLength", 65536, 1, 1048576);

        useEpoll = builder
                .translation("text.autoconfig.online_emotes.option.useEpoll")
                .comment("text.autoconfig.online_emotes.option.useEpoll.@Tooltip")
                .worldRestart()
                .define("useEpoll", Epoll.isAvailable());

        selfPings = builder
                .translation("text.autoconfig.online_emotes.option.selfPings")
                .comment("text.autoconfig.online_emotes.option.selfPings.@Tooltip")
                .define("selfPings", false);

        threads = builder
                .translation("text.autoconfig.online_emotes.option.threads")
                .comment("text.autoconfig.online_emotes.option.threads.@Tooltip")
                .worldRestart()
                .define("threads", 0);
    }

    static { // Early loading for config
        ModContainer activeContainer = ModList.get().getModContainerById(OnlineEmotes.MOD_ID).orElseThrow();
        ModConfigSpec configSpec = ConfigExpectPlatformImpl.CONFIG_SPEC_PAIR.getValue();

        ModConfig modConfig = new ModConfig(ModConfig.Type.CLIENT, configSpec, activeContainer, "online_emotes.toml");
        activeContainer.addConfig(modConfig);

        if (!configSpec.isLoaded()) {
            OnlineEmotes.LOGGER.warn("Config is not loaded?");

            configSpec.acceptConfig(
                    ConfigFileTypeHandler.TOML.reader(FMLPaths.CONFIGDIR.get())
                            .apply(modConfig)
            );
        }
    }

    public static long reconnectionDelay() {
        return CONFIG_SPEC_PAIR.getKey().reconnectionDelay.get();
    }

    public static boolean replaceMessages() {
        return CONFIG_SPEC_PAIR.getKey().replaceMessages.get();
    }

    public static boolean debug() {
        return CONFIG_SPEC_PAIR.getKey().debug.get();
    }

    public static URI address() {
        return URI.create(CONFIG_SPEC_PAIR.getKey().address.get());
    }

    public static int maxContentLength() {
        return CONFIG_SPEC_PAIR.getKey().maxContentLength.get();
    }

    public static boolean useEpoll() {
        return CONFIG_SPEC_PAIR.getKey().useEpoll.get();
    }

    public static boolean selfPings() {
        return CONFIG_SPEC_PAIR.getKey().selfPings.get();
    }

    public static int threads() {
        return CONFIG_SPEC_PAIR.getKey().threads.get();
    }
}
