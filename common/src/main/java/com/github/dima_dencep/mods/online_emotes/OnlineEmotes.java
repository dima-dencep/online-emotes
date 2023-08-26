package com.github.dima_dencep.mods.online_emotes;

import com.github.dima_dencep.mods.online_emotes.config.EmoteConfig;
import com.github.dima_dencep.mods.online_emotes.network.OnlineNetworkInstance;
import io.github.kosmx.emotes.api.proxy.EmotesProxyManager;
import io.github.kosmx.emotes.arch.executor.types.TextImpl;
import io.github.kosmx.emotes.executor.EmoteInstance;
import io.github.kosmx.emotes.executor.dataTypes.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OnlineEmotes {
    public static final Logger LOGGER = LogManager.getFormatterLogger(OnlineEmotes.MOD_ID);
    public static final String MOD_ID = "online_emotes";
    public static OnlineNetworkInstance proxy;

    public void onInitializeClient() {
        EmotesProxyManager.registerProxyInstance(OnlineEmotes.proxy = new OnlineNetworkInstance());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            OnlineEmotes.proxy.disconnectNetty();
            OnlineEmotes.proxy.bootstrap.config().group().shutdownGracefully();
        }));
    }

    public static void sendMessage(boolean debug, Text title, String description) {
        if (debug && !EmoteConfig.INSTANCE.debug) return;

        if (title == null)
            title = EmoteInstance.instance.getDefaults().newTranslationText("text.autoconfig.online_emotes.title");

        if (EmotesExpectPlatform.isEssentialAvailable() && EmoteConfig.INSTANCE.essentialIntegration) {
            EmotesExpectPlatform.sendEssentialMessage(title.getString(), description);

            return;
        }

        try {
            SystemToast toast = SystemToast.create(
                    MinecraftClient.getInstance(),
                    SystemToast.Type.TUTORIAL_HINT,
                    ((TextImpl) title).get(),
                    ((TextImpl) EmoteInstance.instance.getDefaults().newTranslationText(description)).get()
            );

            MinecraftClient.getInstance().getToastManager().add(toast);
        } catch (Throwable ignored) {
        }
    }
}
