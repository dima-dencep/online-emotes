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

import com.github.dima_dencep.mods.online_emotes.client.FancyToast;
import io.github.kosmx.emotes.arch.executor.ClientMethods;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientMethods.class, remap = false)
public abstract class ClientMethodsMixin {

    @Inject(
            method = "sendChatMessage",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void onlineEmotes$sendChatMessage(Component msg, CallbackInfo ci) {
        if (ConfigExpectPlatform.replaceMessages()) {
            FancyToast.sendMessage(null, msg);

            ci.cancel();
        }
    }

    @Inject(
            method = "toastExportMessage",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void onlineEmotes$toastExportMessage(int level, Component text, String msg, CallbackInfo ci) {
        if (ConfigExpectPlatform.replaceMessages()) {
            FancyToast.sendMessage(text, Component.literal(msg));

            ci.cancel();
        }
    }
}
