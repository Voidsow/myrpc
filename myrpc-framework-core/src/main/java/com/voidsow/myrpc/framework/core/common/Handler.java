package com.voidsow.myrpc.framework.core.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.RESP;

public class Handler extends ChannelInboundHandlerAdapter {
    protected ObjectMapper mapper = Utils.getMapper();

    //遇到错误时关闭channel
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Channel channel = ctx.channel();
        if (channel.isActive())
            channel.close();
    }

}
