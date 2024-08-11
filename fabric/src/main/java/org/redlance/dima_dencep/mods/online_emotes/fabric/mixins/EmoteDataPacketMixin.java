/*
 * Copyright 2023 - 2024 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.fabric.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.kosmx.playerAnim.core.data.AnimationBinary;
import io.github.kosmx.emotes.common.network.objects.EmoteDataPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EmoteDataPacket.class, remap = false)
public class EmoteDataPacketMixin {
    @ModifyReturnValue(
            method = "getVer",
            at = @At(
                    value = "RETURN"
            )
    )
    private byte oe$scaleSupport(byte original) {
        return (byte) Math.max(original, AnimationBinary.getCurrentVersion());
    }
}
