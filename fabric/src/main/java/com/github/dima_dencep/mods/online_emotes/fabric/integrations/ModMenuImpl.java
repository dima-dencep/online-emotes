/*
 * Copyright 2023 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package com.github.dima_dencep.mods.online_emotes.fabric.integrations;

import com.github.dima_dencep.mods.online_emotes.fabric.ConfigExpectPlatformImpl;
import com.github.dima_dencep.mods.online_emotes.fabric.FabricOnlineEmotes;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        ConfigExpectPlatformImpl config = FabricOnlineEmotes.MOD_CONFIG; // init

        return parent -> AutoConfig.getConfigScreen(ConfigExpectPlatformImpl.class, parent).get();
    }
}