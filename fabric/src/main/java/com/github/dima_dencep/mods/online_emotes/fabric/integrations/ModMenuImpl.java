package com.github.dima_dencep.mods.online_emotes.fabric.integrations;

import com.github.dima_dencep.mods.online_emotes.config.EmoteConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(EmoteConfig.class, parent).get();
    }
}