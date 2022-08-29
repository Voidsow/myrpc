package com.voidsow.myrpc.framework.core.client;

import com.voidsow.myrpc.framework.core.common.Handler;
import com.voidsow.myrpc.framework.core.common.Invocation;
import com.voidsow.myrpc.framework.core.common.Protocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.*;

public class ClientHandler extends Handler {
    Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Protocol protocol = (Protocol) msg;
        byte[] content = protocol.getContent();
        Invocation invocation = SERIALIZE_FACTORY.deserialize(content, Invocation.class);
        // 忽略非法响应
        if (RESP.containsKey(invocation.getId())) {
            logger.info("接收响应{}", invocation);
            RESP.put(invocation.getId(), invocation);
            ReferenceCountUtil.release(msg);
        }
    }
}
