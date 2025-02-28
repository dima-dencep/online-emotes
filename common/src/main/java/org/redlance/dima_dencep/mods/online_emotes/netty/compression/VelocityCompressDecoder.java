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
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import net.minecraft.network.VarInt;

import java.util.List;

public class VelocityCompressDecoder extends MessageToMessageDecoder<WebSocketFrame> {
    public static final String NAME = "velocity-decompressor";
    private final VelocityCompressor compressor;

    public VelocityCompressDecoder(VelocityCompressor compressor) {
        this.compressor = compressor;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame in, List<Object> out) throws Exception {
        if (in.content().readableBytes() == 0) { // Skip close frame, etc...
            out.add(in.retain());
            return;
        }

        int claimedUncompressedSize = VarInt.read(in.content());
        if (claimedUncompressedSize == 0) {
            // This message is not compressed.
            out.add(in.retain());
            return;
        }

        ByteBuf compatibleIn = MoreByteBufUtils.ensureCompatible(ctx.alloc(), this.compressor, in.content());
        ByteBuf uncompressed = MoreByteBufUtils.preferredBuffer(ctx.alloc(), this.compressor, claimedUncompressedSize);
        try {
            this.compressor.inflate(compatibleIn, uncompressed, claimedUncompressedSize);
            out.add(in.replace(uncompressed));
        } catch (Exception e) {
            uncompressed.release();
            throw e;
        } finally {
            compatibleIn.release();
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.compressor.close();
    }
}
