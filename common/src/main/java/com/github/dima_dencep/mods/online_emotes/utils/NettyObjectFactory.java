/*
 * Copyright 2023 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package com.github.dima_dencep.mods.online_emotes.utils;

import com.github.dima_dencep.mods.online_emotes.ConfigExpectPlatform;
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
        thread.setName(String.format("OnlineEmotes Thread #%d", counter.incrementAndGet()));
        thread.setDaemon(true);
        return thread;
    };

    public static EventLoopGroup newEventLoopGroup() {
        if (Epoll.isAvailable() && ConfigExpectPlatform.useEpoll()) {
            return new EpollEventLoopGroup(ConfigExpectPlatform.threads(), threadFactory);
        } else {
            return new NioEventLoopGroup(ConfigExpectPlatform.threads(), threadFactory);
        }
    }

    public static Class<? extends SocketChannel> getSocketChannel() {
        if (Epoll.isAvailable() && ConfigExpectPlatform.useEpoll()) {
            return EpollSocketChannel.class;
        } else {
            return NioSocketChannel.class;
        }
    }
}
