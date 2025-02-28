/*
 * Copyright 2023 - 2025 dima_dencep.
 *
 * Licensed under the Open Software License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     https://spdx.org/licenses/OSL-3.0.txt
 */

package org.redlance.dima_dencep.mods.online_emotes.netty.compression;

import com.velocitypowered.natives.compression.VelocityCompressor;
import com.velocitypowered.natives.util.MoreByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import net.minecraft.network.VarInt;
import org.redlance.dima_dencep.mods.online_emotes.OnlineEmotesConfig;

import java.util.List;

public class VelocityCompressEncoder extends MessageToMessageEncoder<WebSocketFrame> {
    public static final String NAME = "velocity-compressor";
    private final VelocityCompressor compressor;

    public VelocityCompressEncoder(VelocityCompressor compressor) {
        this.compressor = compressor;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        int uncompressed = msg.content().readableBytes();
        if (uncompressed == 0) { // Skip close frame, etc...
            out.add(msg.retain());
            return;
        }

        ByteBuf allocated = allocateBuffer(ctx, msg.content());
        if (uncompressed < OnlineEmotesConfig.compressionThreshold()) {
            // Under the threshold, there is nothing to do.
            VarInt.write(allocated, 0);
            allocated.writeBytes(msg.content());
        } else {
            VarInt.write(allocated, uncompressed);
            ByteBuf compatibleIn = MoreByteBufUtils.ensureCompatible(ctx.alloc(), this.compressor, msg.content());
            try {
                this.compressor.deflate(compatibleIn, allocated);
            } finally {
                compatibleIn.release();
            }
        }

        out.add(msg.replace(allocated));
    }

    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg) {
        // We allocate bytes to be compressed plus 1 byte. This covers two cases:
        //
        // - Compression
        //    According to https://github.com/ebiggers/libdeflate/blob/master/libdeflate.h#L103,
        //    if the data compresses well (and we do not have some pathological case) then the maximum
        //    size the compressed size will ever be is the input size minus one.
        // - Uncompressed
        //    This is fairly obvious - we will then have one more than the uncompressed size.
        int initialBufferSize = msg.readableBytes() + 1;
        return MoreByteBufUtils.preferredBuffer(ctx.alloc(), this.compressor, initialBufferSize);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.compressor.close();
    }
}
