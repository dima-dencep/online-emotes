/*
 * Copyright 2023 - 2025 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.neoforge;

import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

@SuppressWarnings("unused")
public class OnlineEmotesPlatformImpl {
    public static String getModVersion(String modid) {
        ModFileInfo info = LoadingModList.get().getModFileById(modid);
        if (info == null) return modid.toUpperCase() + "-UNKNOWN-NEOFORGE";
        return info.versionString();
    }

    @SuppressWarnings("UnstableApiUsage")
    public static @Nullable Path getModFile(String modid) {
        ModFileInfo inf = LoadingModList.get().getModFileById(modid);
        if (inf == null) return null;
        return inf.getFile().getFilePath();
    }
}
