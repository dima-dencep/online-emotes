package com.github.dima_dencep.mods.online_emotes.forge;

import gg.essential.api.EssentialAPI;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;

public class EmotesExpectPlatformImpl {
    public static boolean isEssentialAvailable() {
        try {
            return ModList.get().isLoaded("essential");
        } catch (Throwable t) {
            return LoadingModList.get().getModFileById("essential") != null;
        }
    }

    public static void sendEssentialMessage(String title, String description) {
        try {
            EssentialAPI.getNotifications().push(title, description);
        } catch (Throwable ignored) {

        }
    }

    public static String getModVersion() {
        ModFileInfo container = LoadingModList.get().getModFileById(ForgeOnlineEmotes.MOD_ID);

        if (container != null) {
            return container.versionString();
        }

        return "null-forge";
    }
}
