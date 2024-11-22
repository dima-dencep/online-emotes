/*
 * Copyright 2023 - 2024 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.common.Mod;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotesConfig;

@Mod(value = OnlineEmotes.MOD_ID, dist = Dist.CLIENT)
public class ForgeOnlineEmotes extends OnlineEmotes {
    public ForgeOnlineEmotes(ModContainer container) {
        container.registerConfig(ModConfig.Type.STARTUP,
                OnlineEmotesConfig.CONFIG_SPEC_PAIR.getValue(), "online_emotes.toml"
        );
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        super.onInitializeClient();

        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingIn.class,
                event -> onLoggingIn(event.getPlayer(), event.getConnection())
        );

        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class,
                event -> onLoggingOut(event.getPlayer(), event.getConnection())
        );
    }
}
