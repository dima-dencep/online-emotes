/*
 * Copyright 2023 - 2025 dima_dencep.
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

import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("unused")
public class OnlineEmotesPlatformImpl {
    public static String getModVersion(String modid) {
        return FabricLoader.getInstance().getModContainer(modid)
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getVersion)
                .map(Version::getFriendlyString)
                .orElse(modid.toUpperCase() + "-UNKNOWN-FABRIC");
    }

    public static @Nullable Path getModFile(String modid) {
        return FabricLoader.getInstance().getModContainer(modid)
                .map(ModContainer::getOrigin)
                .map(ModOrigin::getPaths)
                .map(List::getFirst)
                .orElse(null);
    }
}
