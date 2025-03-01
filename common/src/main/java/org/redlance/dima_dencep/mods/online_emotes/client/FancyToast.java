/*
 * Copyright 2023 - 2025 dima_dencep.
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
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;

import java.util.ArrayList;
import java.util.List;

public class FancyToast implements Toast {
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath("online-emotes", "icon.png");
    public static final Component TITLE = Component.translatable("online_emotes.configuration.title");

    protected final Component title;
    private final List<FormattedCharSequence> messageLines = new ArrayList<>();

    protected FancyToast(Component title, List<FormattedCharSequence> msg) {
        this.title = title;
        this.messageLines.addAll(msg);
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

        for (int j = 0; j < this.messageLines.size(); j++) {
            guiGraphics.drawString(textRenderer, this.messageLines.get(j), 30, (title != null ? 18 : 16) + j * 12, -1, false);
        }

        return timeSinceLastVisible < (double) 1500L * manager.getNotificationDisplayTimeMultiplier() ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public int width() {
        Font font = Minecraft.getInstance().font;

        int headerSize = font.width(this.title);
        int messageSize = this.messageLines.stream()
                .mapToInt(font::width)
                .max()
                .orElse(200);

        return 37 + Math.max(headerSize, messageSize);
    }

    @Override
    public int height() {
        return 20 + Math.max(this.messageLines.size(), 1) * 12;
    }

    @Override
    public int slotCount() {
        return Math.min(Toast.super.slotCount(), 5);
    }

    public static void sendMessage(Component description) {
        sendMessage(FancyToast.TITLE, description);
    }

    public static void sendMessage(Component title, Component description) {
        OnlineEmotes.LOGGER.info("Toast message: {}", description.getString());
        Minecraft.getInstance().getToasts().addToast(new FancyToast(title,
                Minecraft.getInstance().font.split(description, 200)
        ));
    }
}
