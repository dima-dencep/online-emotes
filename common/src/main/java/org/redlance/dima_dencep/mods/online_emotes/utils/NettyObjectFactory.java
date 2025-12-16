/*
 * Copyright 2023 - 2025 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.utils;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueIoHandler;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioIoHandler;
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
        return new MultiThreadIoEventLoopGroup(threadFactory, ioHandlerFactory());
    }

    private static IoHandlerFactory ioHandlerFactory() {
        if (KQueue.isAvailable()) return KQueueIoHandler.newFactory();
        if (Epoll.isAvailable()) return EpollIoHandler.newFactory();
        return NioIoHandler.newFactory();
    }

    public static Class<? extends SocketChannel> getSocketChannel() {
        if (KQueue.isAvailable()) return KQueueSocketChannel.class;
        if (Epoll.isAvailable()) return EpollSocketChannel.class;
        return NioSocketChannel.class;
    }
}
