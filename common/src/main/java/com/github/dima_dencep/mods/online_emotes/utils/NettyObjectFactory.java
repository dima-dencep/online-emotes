package com.github.dima_dencep.mods.online_emotes.utils;

import com.github.dima_dencep.mods.online_emotes.config.EmoteConfig;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyObjectFactory {
    private static final AtomicInteger counter = new AtomicInteger();
    private static final ThreadFactory threadFactory = (runnable) -> {
        Thread thread = new Thread(runnable);
        thread.setName(String.format("Netty Thread #%d", counter.incrementAndGet()));
        thread.setDaemon(true);
        return thread;
    };

    public static EventLoopGroup newEventLoopGroup() {
        if (Epoll.isAvailable() && EmoteConfig.INSTANCE.useEpoll) {
            return new EpollEventLoopGroup(EmoteConfig.INSTANCE.threads, threadFactory);
        } else {
            return new NioEventLoopGroup(EmoteConfig.INSTANCE.threads, threadFactory);
        }
    }

    public static Class<? extends SocketChannel> getSocketChannel() {
        if (Epoll.isAvailable() && EmoteConfig.INSTANCE.useEpoll) {
            return EpollSocketChannel.class;
        } else {
            return NioSocketChannel.class;
        }
    }
}
