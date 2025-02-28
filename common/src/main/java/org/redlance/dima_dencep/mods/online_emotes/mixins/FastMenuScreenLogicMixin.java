/*
 * Copyright 2023 - 2025 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotes;
import io.github.kosmx.emotes.arch.screen.ingame.FastMenuScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotesConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FastMenuScreen.class)
public abstract class FastMenuScreenLogicMixin extends Screen {
    @Shadow
    @Final
    private HeaderAndFooterLayout layout;
    @Shadow
    @Final
    private static Component WARN_ONLY_PROXY;
    @Unique
    private static final Component OE_ONLYTHIS = Component.translatable("online_emotes.warnings.onlyThis");
    @Unique
    private static final Component OE_RECONNECT = Component.translatable("online_emotes.button.reconect");
    @Unique
    private Button oe$reconnectButton;

    protected FastMenuScreenLogicMixin(Component title) {
        super(title);
    }

    @WrapOperation(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/layouts/HeaderAndFooterLayout;addTitleHeader(Lnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/Font;)V"
            )
    )
    public void onlineEmotes$emotes_renderScreen(HeaderAndFooterLayout instance, Component message, Font font, Operation<Void> original) {
        if (OnlineEmotes.proxy.isActive() && message == WARN_ONLY_PROXY) {
            message = OE_ONLYTHIS;
        }
        original.call(instance, message, font);
    }

    @Inject(
            method = "renderBlurredBackground",
            at = @At(
                    value = "HEAD"
            )
    )
    public void onlineEmotes$emotes_renderScreen(float f, CallbackInfo ci) {
        if (oe$reconnectButton != null) {
            oe$reconnectButton.active = !OnlineEmotes.proxy.isActive();
        }
    }

    @WrapOperation(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/layouts/HeaderAndFooterLayout;addToFooter(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;"
            )
    )
    public <T extends LayoutElement> T addButton(HeaderAndFooterLayout instance, T child, Operation<T> original) {
        if (!OnlineEmotesConfig.debug()) {
            return original.call(instance, child);
        }

        LinearLayout linearLayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(Button.DEFAULT_SPACING));

        this.oe$reconnectButton = linearLayout.addChild(Button.builder(OE_RECONNECT, (button) ->
                OnlineEmotes.proxy.connect()
        ).width(Button.SMALL_WIDTH).build());

        return linearLayout.addChild(child);
    }
}
