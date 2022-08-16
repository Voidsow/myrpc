package com.voidsow.myrpc.framework.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class Decoder extends ByteToMessageDecoder {

    static int HEADER_LENGTH = 2 + 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        if (byteBuf.readableBytes() >= HEADER_LENGTH) {
            byteBuf.markReaderIndex();
            if (byteBuf.readShort() != Constant.MAGIC_NUMBER) {
                channelHandlerContext.close();
                return;
            }
            int msgLength = byteBuf.readInt();
            if (byteBuf.readableBytes() >= msgLength) {
                byte[] content;
                if (byteBuf.hasArray())
                    content = byteBuf.array();
                else {
                    content = new byte[msgLength];
                    byteBuf.readBytes(content);
                }
                out.add(new Protocol(content));
            } else {
                byteBuf.resetReaderIndex();
            }
        }
    }
}
