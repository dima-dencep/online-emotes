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
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.NonNull;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;

import java.util.ArrayList;
import java.util.List;

public class FancyToast implements Toast {
    public static final Identifier ICON = Identifier.fromNamespaceAndPath("online-emotes", "icon.png");
    public static final Component TITLE = Component.translatable("online_emotes.configuration.title");

    protected final Component title;
    private final List<FormattedCharSequence> messageLines = new ArrayList<>();

    private Visibility visibility = Visibility.SHOW;

    protected FancyToast(Component title, List<FormattedCharSequence> msg) {
        this.title = title;
        this.messageLines.addAll(msg);
    }

    @Override
    public @NonNull Visibility getWantedVisibility() {
        return this.visibility;
    }

    @Override
    public void update(ToastManager manager, long timeSinceLastVisible) {
        this.visibility = timeSinceLastVisible < (double) 1500L * manager.getNotificationDisplayTimeMultiplier() ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public void render(GuiGraphics guiGraphics, @NonNull Font textRenderer, long timeSinceLastVisible) {
        guiGraphics.fill(0, 0, width(), height() - 1, -1207959552);
        guiGraphics.fill(0, height() - 1, width(), height(), 0xFFfc1a47);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ICON, 8, 8, 0.0F, 0.0F, 16, 16, 16, 16);

        if (this.title != null) {
            guiGraphics.drawString(textRenderer, this.title, 30, 7, -1, false);
        }

        for (int j = 0; j < this.messageLines.size(); j++) {
            guiGraphics.drawString(textRenderer, this.messageLines.get(j), 30, (title != null ? 18 : 16) + j * 12, -1, false);
        }
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
    public int occcupiedSlotCount() {
        return Math.min(Toast.super.occcupiedSlotCount(), 5);
    }

    public static void sendMessage(Component description) {
        sendMessage(FancyToast.TITLE, description);
    }

    public static void sendMessage(Component title, Component description) {
        OnlineEmotes.LOGGER.info("Toast message: {}", description.getString());
        List<FormattedCharSequence> msg = Minecraft.getInstance().submit(() ->
                Minecraft.getInstance().font.split(description, 200)
        ).join();
        Minecraft.getInstance().getToastManager().addToast(new FancyToast(title, msg));
    }
}
