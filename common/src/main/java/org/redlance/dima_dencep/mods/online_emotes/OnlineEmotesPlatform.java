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
