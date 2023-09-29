package com.github.dima_dencep.mods.online_emotes.mixins;

import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import com.github.dima_dencep.mods.online_emotes.config.EmoteConfig;
import io.github.kosmx.emotes.executor.EmoteInstance;
import io.github.kosmx.emotes.executor.dataTypes.Text;
import io.github.kosmx.emotes.executor.dataTypes.screen.widgets.IButton;
import io.github.kosmx.emotes.main.screen.AbstractScreenLogic;
import io.github.kosmx.emotes.main.screen.IScreenSlave;
import io.github.kosmx.emotes.main.screen.ingame.FastMenuScreenLogic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FastMenuScreenLogic.class, remap = false)
public abstract class FastMenuScreenLogicMixin<MATRIX, SCREEN> extends AbstractScreenLogic<MATRIX, SCREEN> {

    @Unique
    private static final Text onlineEmotes$warn = EmoteInstance.instance.getDefaults().newTranslationText("online_emotes.warnings.onlyThis");

    @Unique
    private IButton<?> reconnectButton;

    @Shadow
    @Final
    private static Text warn_only_proxy;

    protected FastMenuScreenLogicMixin(IScreenSlave screen) {
        super(screen);
    }

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

    @Inject(
            method = "emotes_renderScreen",
            at = @At(
                    value = "HEAD"
            )
    )
    public void onlineEmotes$emotes_renderScreen(MATRIX matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (reconnectButton != null) {
            reconnectButton.setActive(!OnlineEmotes.proxy.isActive());
        }
    }

    @Inject(
            method = "emotes_initScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lio/github/kosmx/emotes/main/screen/IScreenSlave;addButtonsToChildren()V"
            )
    )
    public void addButton(CallbackInfo ci) {
        if (EmoteConfig.INSTANCE.debug) {
            this.screen.addToButtons(
                    reconnectButton = newButton(
                            screen.getWidth() - 120,
                            screen.getHeight() - 55,
                            96, 20,
                            EmoteInstance.instance.getDefaults().newTranslationText("online-emotes.buttons.reconnect"),
                            (button) -> OnlineEmotes.proxy.connect()
                    )
            );
        }
    }
}
