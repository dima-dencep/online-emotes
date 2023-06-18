package com.github.dima_dencep.mods.online_emotes.mixins;

import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import io.github.kosmx.emotes.arch.executor.AbstractClientMethods;
import io.github.kosmx.emotes.arch.executor.types.TextImpl;
import io.github.kosmx.emotes.executor.dataTypes.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientMethods.class)
public abstract class ClientMethodsMixin {

    @Inject(
            method = "sendChatMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V"
            ),
            cancellable = true
    )
    public void sendChatMessage(Text msg, CallbackInfo ci) {
        if (OnlineEmotes.config.replaceMessages) {
            OnlineEmotes.sendMessage(false, net.minecraft.text.Text.translatable("text.autoconfig.online_emotes.title"), ((TextImpl) msg).get());

            ci.cancel();
        }
    }

    @Inject(
            method = "toastExportMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/toast/SystemToast;add(Lnet/minecraft/client/toast/ToastManager;Lnet/minecraft/client/toast/SystemToast$Type;Lnet/minecraft/text/Text;Lnet/minecraft/text/Text;)V"
            ),
            cancellable = true
    )
    public void toastExportMessage(int level, Text text, String msg, CallbackInfo ci) {
        if (OnlineEmotes.config.replaceMessages) {
            OnlineEmotes.sendMessage(false, ((TextImpl) text).get(), net.minecraft.text.Text.of(msg));

            ci.cancel();
        }
    }
}
