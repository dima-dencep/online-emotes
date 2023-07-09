package com.github.dima_dencep.mods.online_emotes.fabric;

import gg.essential.api.EssentialAPI;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.Optional;

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

    public static String getModVersion() {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(FabricOnlineEmotes.MOD_ID);

        if (container.isPresent()) {
            return container.get().getMetadata().getVersion().getFriendlyString();
        }

        return "null-fabric";
    }
}
