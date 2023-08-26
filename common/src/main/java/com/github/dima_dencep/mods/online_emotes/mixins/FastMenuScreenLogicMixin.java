package com.github.dima_dencep.mods.online_emotes.mixins;

import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import io.github.kosmx.emotes.executor.EmoteInstance;
import io.github.kosmx.emotes.executor.dataTypes.Text;
import io.github.kosmx.emotes.main.screen.ingame.FastMenuScreenLogic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = FastMenuScreenLogic.class, remap = false)
public class FastMenuScreenLogicMixin {

    @Unique
    private static final Text onlineEmotes$warn = EmoteInstance.instance.getDefaults().newTranslationText("online_emotes.warnings.onlyThis");

    @Shadow
    @Final
    private static Text warn_only_proxy;

    @ModifyArg(
            method = "emotes_renderScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lio/github/kosmx/emotes/main/screen/ingame/FastMenuScreenLogic;drawCenteredText(Ljava/lang/Object;Lio/github/kosmx/emotes/executor/dataTypes/Text;III)V"
            ),
            index = 1
    )
    public Text onlineEmotes$emotes_renderScreen(Text par2) {
        if (par2 == warn_only_proxy && OnlineEmotes.proxy.isActive()) {
            return onlineEmotes$warn;
        }
        return par2;
    }
}
