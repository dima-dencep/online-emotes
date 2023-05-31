package com.github.dima_dencep.mods.online_emotes.forge;

import gg.essential.api.EssentialAPI;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;

public class EmotesExpectPlatformImpl {
    public static boolean isEssentialAvailable() {
        try {
            return ModList.get().isLoaded("");
        } catch (Throwable t) {
            return LoadingModList.get().getModFileById("") != null;
        }
    }

    public static void sendEssentialMessage(String title, String description) {
        try {
            EssentialAPI.getNotifications().push(title, description);
        } catch (Throwable ignored) {

        }
    }
}
