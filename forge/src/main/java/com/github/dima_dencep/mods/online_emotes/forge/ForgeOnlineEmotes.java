package com.github.dima_dencep.mods.online_emotes.forge;

import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import com.github.dima_dencep.mods.online_emotes.config.EmoteConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;

@Mod(OnlineEmotes.MOD_ID)
public class ForgeOnlineEmotes extends OnlineEmotes {
    public ForgeOnlineEmotes() {
        ModLoadingContext.get().registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> NetworkConstants.IGNORESERVERONLY,
                        (a, b) -> true
                )
        );

        super.onInitializeClient();

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, screen) -> AutoConfig.getConfigScreen(EmoteConfig.class, screen).get()
                )
        );
    }

    @SubscribeEvent
    public void onJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (proxy.isActive()) {
            proxy.sendConfigCallback();
        } else {
            proxy.connect();
        }
    }

    @SubscribeEvent
    public void onExit(ClientPlayerNetworkEvent.LoggingOut event) {
        if (proxy.isActive()) {
            proxy.disconnect();
        }
    }
}
