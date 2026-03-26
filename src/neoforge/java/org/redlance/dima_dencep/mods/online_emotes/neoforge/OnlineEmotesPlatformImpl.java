/*
 * Copyright 2023 - 2026 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.neoforge;

import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import org.jetbrains.annotations.Nullable;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotesPlatform;

import java.nio.file.Path;

@SuppressWarnings("UnstableApiUsage")
public final class OnlineEmotesPlatformImpl implements OnlineEmotesPlatform {
    @Override
    public String getModVersion(String modid) {
        ModFileInfo info = FMLLoader.getCurrent().getLoadingModList().getModFileById(modid);
        if (info == null) return modid.toUpperCase() + "-UNKNOWN-NEOFORGE";
        return info.versionString();
    }

    @Override
    public @Nullable Path getModFile(String modid) {
        ModFileInfo inf = FMLLoader.getCurrent().getLoadingModList().getModFileById(modid);
        if (inf == null) return null;
        return inf.getFile().getFilePath();
    }

    @Override
    public Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get().resolve(OnlineEmotes.MOD_ID + ".json");
    }

    @Override
    public boolean isServiceActive() {
        try {
            Class.forName("net.neoforged.fml.loading.FMLLoader");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
