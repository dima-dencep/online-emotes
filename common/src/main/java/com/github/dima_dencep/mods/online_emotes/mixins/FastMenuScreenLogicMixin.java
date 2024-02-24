/*
 * Copyright 2023 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package com.github.dima_dencep.mods.online_emotes.mixins;

import com.github.dima_dencep.mods.online_emotes.ConfigExpectPlatform;
import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import io.github.kosmx.emotes.arch.screen.EmoteConfigScreen;
import io.github.kosmx.emotes.arch.screen.ingame.FastMenuScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FastMenuScreen.class)
public abstract class FastMenuScreenLogicMixin extends EmoteConfigScreen {
    @Shadow
    @Final
    private static Component warn_only_proxy;
    @Unique
    private static final Component oe$warn = Component.translatable("online_emotes.warnings.onlyThis");
    @Unique
    private static final Component oe$reconnect = Component.translatable("online_emotes.button.reconect");
    @Unique
    private Button oe$reconnectButton;

    protected FastMenuScreenLogicMixin(@NotNull Component title, @Nullable Screen parent) {
        super(title, parent);
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"
            ),
            index = 1
    )
    public Component onlineEmotes$emotes_renderScreen(Component text) {
        if (text == warn_only_proxy && OnlineEmotes.proxy.isActive()) {
            return oe$warn;
        }

        return text;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "HEAD"
            )
    )
    public void onlineEmotes$emotes_renderScreen(GuiGraphics matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (oe$reconnectButton != null) {
            oe$reconnectButton.active = !OnlineEmotes.proxy.isActive();
        }
    }

    @Inject(
            method = "init",
            at = @At(
                    value = "TAIL"
            )
    )
    public void addButton(CallbackInfo ci) {
        if (!ConfigExpectPlatform.debug())
            return;

        oe$reconnectButton = addRenderableWidget(Button.builder(oe$reconnect, (button) ->
                OnlineEmotes.proxy.connect()
        ).pos(getWidth() - 120, getHeight() - 55).size(96, 20).build());
    }
}
