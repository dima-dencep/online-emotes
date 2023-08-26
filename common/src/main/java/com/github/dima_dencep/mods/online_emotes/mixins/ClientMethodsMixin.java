package com.github.dima_dencep.mods.online_emotes.mixins;

import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import com.github.dima_dencep.mods.online_emotes.config.EmoteConfig;
import io.github.kosmx.emotes.arch.executor.AbstractClientMethods;
import io.github.kosmx.emotes.executor.dataTypes.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractClientMethods.class, remap = false)
public abstract class ClientMethodsMixin {

    @Inject(
            method = "sendChatMessage",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void onlineEmotes$sendChatMessage(Text msg, CallbackInfo ci) {
        if (EmoteConfig.INSTANCE.replaceMessages) {
            OnlineEmotes.sendMessage(false, null, msg.getString());

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
    public void onlineEmotes$toastExportMessage(int level, Text text, String msg, CallbackInfo ci) {
        if (EmoteConfig.INSTANCE.replaceMessages) {
            OnlineEmotes.sendMessage(false, text, msg);

            ci.cancel();
        }
    }
}
