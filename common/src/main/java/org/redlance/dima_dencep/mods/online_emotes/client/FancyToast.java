/*
 * Copyright 2023 - 2024 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;

public class FancyToast implements Toast {
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath("online-emotes", "icon.png");
    public static final Component TITLE = Component.translatable("online_emotes.configuration.title");

    protected final Component title;
    protected final Component msg;

    protected FancyToast(Component title, Component msg) {
        this.title = title;
        this.msg = msg;
    }

    @Override
    public @NotNull Visibility render(GuiGraphics guiGraphics, ToastComponent manager, long timeSinceLastVisible) {
        guiGraphics.fill(0, 0, width(), height() - 1, -1207959552);
        guiGraphics.fill(0, height() - 1, width(), height(), 0xFFfc1a47);
        guiGraphics.blit(ICON, 8, 8, 0.0F, 0.0F, 16, 16, 16, 16);

        Font textRenderer = manager.getMinecraft().font;

        if (this.title != null) {
            guiGraphics.drawString(textRenderer, this.title, 30, 7, 16777215, false);
        }

        guiGraphics.drawString(textRenderer, msg, 30, title != null ? 18 : 16, 16777215, false);

        return timeSinceLastVisible < (double) 1500L * manager.getNotificationDisplayTimeMultiplier() ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public int width() {
        if (this.msg == null) {
            return Toast.super.width();
        }

        Font font = Minecraft.getInstance().font;

        return Math.max(
                font.width(this.msg), font.width(this.title)
        ) + 38;
    }

    public static void sendMessage(Component description) {
        sendMessage(FancyToast.TITLE, description);
    }

    public static void sendMessage(Component title, Component description) {
        OnlineEmotes.LOGGER.info(description.getString());

        Minecraft.getInstance().getToasts()
                .addToast(new FancyToast(title, description));
    }
}
