package com.github.dima_dencep.mods.online_emotes.utils;

import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import com.github.dima_dencep.mods.online_emotes.config.EmoteConfig;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Reconnector {
    private static final AtomicReference<ScheduledFuture<?>> future = new AtomicReference<>();

    public static void start(EventLoopGroup runIn) {
        OnlineEmotes.LOGGER.info("Starting reconnector...");

        ScheduledFuture<?> oldFuture = future.getAndSet(
                runIn.scheduleAtFixedRate(Reconnector::reconnect, 0L, EmoteConfig.INSTANCE.reconnectionDelay, TimeUnit.SECONDS)
        );

        cancel(oldFuture);
    }

    public static void stop() {
        cancel(future.get());
    }

    private static void reconnect() {
        if (!OnlineEmotes.proxy.isActive()) {
            OnlineEmotes.LOGGER.info("Try reconnecting...");

            OnlineEmotes.proxy.connect();
        }
    }

    private static void cancel(ScheduledFuture<?> oldFuture) {
        if (oldFuture != null && !oldFuture.isCancelled()) {
            oldFuture.cancel(true);

            OnlineEmotes.LOGGER.warn("Reconector canceled!");
        }
    }
}
