/*
 * Copyright 2023 - 2026 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.client;

import io.github.kosmx.emotes.arch.gui.toast.EmotecraftToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;

public class FancyToast extends EmotecraftToast {
    public static final Identifier ICON = Identifier.fromNamespaceAndPath("online-emotes", "icon.png");
    public static final Component TITLE = Component.translatable("online_emotes.configuration.title");

    protected FancyToast(ToastManager manager, @Nullable Component message) {
        super(manager, ICON, 1500L, TITLE, message);
    }

    public static void sendMessage(Component description) {
        OnlineEmotes.LOGGER.info("Toast message: {}", description.getString());

        Runnable task = () -> {
            ToastManager manager = Minecraft.getInstance().gui.toastManager();
            manager.addToast(new FancyToast(manager, description));
        };

        if (Minecraft.getInstance().isSameThread()) {
            task.run();
        } else {
            Minecraft.getInstance().execute(task);
        }
    }
}
