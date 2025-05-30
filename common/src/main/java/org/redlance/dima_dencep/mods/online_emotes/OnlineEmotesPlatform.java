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

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class OnlineEmotesPlatform {
    @ExpectPlatform
    @Contract
    public static String getModVersion(String modid) {
        throw new AssertionError();
    }

    @ExpectPlatform
    @Contract
    public static @Nullable Path getModFile(String modid) {
        throw new AssertionError();
    }
}
