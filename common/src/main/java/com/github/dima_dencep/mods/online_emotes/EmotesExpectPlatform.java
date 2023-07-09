package com.github.dima_dencep.mods.online_emotes;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class EmotesExpectPlatform {
    @ExpectPlatform
    public static boolean isEssentialAvailable() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendEssentialMessage(String title, String description) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static String getModVersion() {
        throw new AssertionError();
    }
}
