package com.voidsow.myrpc.framework.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class Encoder extends MessageToByteEncoder<Protocol> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Protocol protocol, ByteBuf byteBuf) throws Exception {
        byteBuf.writeShort(protocol.getMagicNumber());
        byteBuf.writeInt(protocol.getLength());
        byteBuf.writeBytes(protocol.getContent());
    }
}
