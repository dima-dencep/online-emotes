package com.github.dima_dencep.mods.online_emotes;

import com.github.dima_dencep.mods.online_emotes.config.EmoteConfig;
import com.github.dima_dencep.mods.online_emotes.network.OnlineProxyImpl;
import io.github.kosmx.emotes.api.proxy.EmotesProxyManager;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OnlineEmotes {
    public static final Logger logger = LogManager.getFormatterLogger(OnlineEmotes.MOD_ID);
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final String MOD_ID = "online_emotes";
    public static OnlineProxyImpl proxy;
    public static EmoteConfig config;

    public void onInitializeClient() {
        OnlineEmotes.config = AutoConfig.register(EmoteConfig.class, Toml4jConfigSerializer::new).getConfig();
        OnlineEmotes.proxy = new OnlineProxyImpl();

        EmotesProxyManager.registerProxyInstance(OnlineEmotes.proxy);
    }

    public static void sendMessage(Text title, Text description) {
        if (config.essentialIntegration && EmotesExpectPlatform.isEssentialAvailable()) {
            EmotesExpectPlatform.sendEssentialMessage(title.getString(), description.getString());

            return;
        }

        client.getToastManager().add(SystemToast.create(client, SystemToast.Type.TUTORIAL_HINT, title, description));
    }
}
