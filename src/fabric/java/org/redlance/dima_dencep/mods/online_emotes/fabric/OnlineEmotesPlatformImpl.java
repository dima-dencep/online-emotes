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

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.ModOrigin;
import org.jetbrains.annotations.Nullable;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotesPlatform;

import java.nio.file.Path;
import java.util.List;

public final class OnlineEmotesPlatformImpl implements OnlineEmotesPlatform {
    @Override
    public String getModVersion(String modid) {
        return FabricLoader.getInstance().getModContainer(modid)
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getVersion)
                .map(Version::getFriendlyString)
                .orElse(modid.toUpperCase() + "-UNKNOWN-FABRIC");
    }

    @Override
    public @Nullable Path getModFile(String modid) {
        return FabricLoader.getInstance().getModContainer(modid)
                .map(ModContainer::getOrigin)
                .map(ModOrigin::getPaths)
                .map(List::getFirst)
                .orElse(null);
    }

    @Override
    public Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(OnlineEmotes.MOD_ID + ".json");
    }

    @Override
    public boolean isServiceActive() {
        try {
            Class.forName("net.fabricmc.loader.api.FabricLoader");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
