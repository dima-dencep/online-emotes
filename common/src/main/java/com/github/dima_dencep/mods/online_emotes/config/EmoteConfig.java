package com.github.dima_dencep.mods.online_emotes.config;

import com.github.dima_dencep.mods.online_emotes.EmotesExpectPlatform;
import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import io.netty.channel.epoll.Epoll;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = OnlineEmotes.MOD_ID)
public class EmoteConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public long reconnectionDelay = 15L;

    public boolean replaceMessages = true;

    public boolean debug = false;

    @ConfigEntry.Gui.Excluded
    @ConfigEntry.Category("netty")
    @ConfigEntry.Gui.RequiresRestart
    public String address = "wss://api.constructlegacy.ru:443/websockets/online-emotes";

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("netty")
    @ConfigEntry.Gui.RequiresRestart
    public boolean useEpoll = Epoll.isAvailable();

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("netty")
    @ConfigEntry.Gui.RequiresRestart
    public int threads = 0;

    @ConfigEntry.Category("integrations")
    public boolean essentialIntegration = true;
}
