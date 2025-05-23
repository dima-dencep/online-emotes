package org.redlance.dima_dencep.mods.online_emotes.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
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
                .map(ModContainer::getRootPaths)
                .map(List::getFirst)
                .orElse(null);
    }
}
