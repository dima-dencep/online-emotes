package com.github.dima_dencep.mods.online_emotes.fabric;

import gg.essential.api.EssentialAPI;
import net.fabricmc.loader.api.FabricLoader;

public class EmotesExpectPlatformImpl {
    public static boolean isEssentialAvailable() {
        return FabricLoader.getInstance().isModLoaded("essential-loader");
    }

    public static void sendEssentialMessage(String title, String description) {
        try {
            EssentialAPI.getNotifications().push(title, description);
        } catch (Throwable ignored) {

        }
    }
}
