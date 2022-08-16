package com.voidsow.myrpc.framework.core.client;

import com.voidsow.myrpc.framework.core.common.Handler;
import com.voidsow.myrpc.framework.core.common.Invocation;
import com.voidsow.myrpc.framework.core.common.Protocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.RESP;

public class ClientHandler extends Handler {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Protocol protocol = (Protocol) msg;
        byte[] content = protocol.getContent();
        Invocation invocation = mapper.readValue(content, Invocation.class);
        // 忽略非法响应
        if (RESP.containsKey(invocation.getId())) {
            RESP.put(invocation.getId(), invocation);
            ReferenceCountUtil.release(msg);
        }
    }
}
