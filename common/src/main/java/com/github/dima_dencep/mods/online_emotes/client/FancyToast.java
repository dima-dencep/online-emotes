/*
 * Copyright 2023 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package com.github.dima_dencep.mods.online_emotes.client;

import com.github.dima_dencep.mods.online_emotes.ConfigExpectPlatform;
import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FancyToast implements Toast {
    private static final ResourceLocation IDENTIFIER = new ResourceLocation("online-emotes", "icon.png");
    private static final FancyToast FANCY_TOAST = new FancyToast();

    private boolean changed;
    private long lastChanged;
    private boolean locked;
    public Component title;
    public Component msg;

    @Override
    public @NotNull Visibility render(GuiGraphics guiGraphics, ToastComponent manager, long timeSinceLastVisible) {
        if (msg == null)
            return Visibility.HIDE;

        if (this.changed) {
            this.lastChanged = timeSinceLastVisible;
            this.changed = false;
        }

        guiGraphics.fill(0, 0, width(), height() - 1, -1207959552);
        guiGraphics.fill(0, height() - 1, width(), height(), 0xFFfc1a47);
        guiGraphics.blit(IDENTIFIER, 8, 8, 0.0F, 0.0F, 16, 16, 16, 16);

        Font textRenderer = manager.getMinecraft().font;

        if (title != null)
            guiGraphics.drawString(textRenderer, title, 30, 7, 16777215, false);

        guiGraphics.drawString(textRenderer, msg, 30, title != null ? 18 : 16, 16777215, false);

        if (this.locked) {
            return Visibility.SHOW;
        }

        return timeSinceLastVisible - this.lastChanged < (double) 1500L * manager.getNotificationDisplayTimeMultiplier() ? Visibility.SHOW : Visibility.HIDE;
    }

    public static void sendMessage(Component title, Component description) {
        sendMessage(false, false, false, title, description);
    }

    public static void sendMessage(boolean debug, boolean locked, boolean replace, Component title, Component description) {
        OnlineEmotes.LOGGER.info(description.getString());

        if (debug && !ConfigExpectPlatform.debug()) return;

        if (title == null)
            title = OnlineEmotes.TITLE;

        ToastComponent toastManager = Minecraft.getInstance().getToasts();

        if (FANCY_TOAST.locked && !replace) {
            FancyToast fancyToast = new FancyToast();

            fancyToast.title = title;
            fancyToast.msg = description;
            fancyToast.locked = false;

            toastManager.addToast(fancyToast);

            return;
        }

        FANCY_TOAST.title = title;
        FANCY_TOAST.msg = description;
        FANCY_TOAST.locked = locked;
        FANCY_TOAST.changed = true;

        if (toastManager.getToast(FancyToast.class, FancyToast.NO_TOKEN) == null) {
            toastManager.addToast(FANCY_TOAST);
        }
    }
}
