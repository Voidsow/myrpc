package com.voidsow.myrpc.framework.core.server;

import com.voidsow.myrpc.framework.core.common.Handler;
import com.voidsow.myrpc.framework.core.common.Invocation;
import com.voidsow.myrpc.framework.core.common.Protocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static com.voidsow.myrpc.framework.core.common.cache.ServerCache.PROVIDER_CLASS;

public class ServerHandler extends Handler {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        Protocol protocol = (Protocol) msg;
        String json = new String(protocol.getContent());
        Invocation invocation = mapper.readValue(json, Invocation.class);
        logger.info("处理来自{}的请求{}", channel.remoteAddress(), invocation);
        //获取service对应的provider
        Object service = PROVIDER_CLASS.get(invocation.getService());
        if (service == null)
            throw new RuntimeException("service not found");
        Method[] methods = service.getClass().getDeclaredMethods();
        Object result = null;
        for (Method method : methods) {
            if (method.getName().equals(invocation.getMethod())) {
                if (method.getReturnType().equals(Void.TYPE)) {
                    method.invoke(service, invocation.getParameters());
                } else
                    result = method.invoke(service, invocation.getParameters());
                break;
            }
        }
        invocation.setResponse(result);
        protocol.setContent(mapper.writeValueAsString(invocation).getBytes());
        protocol.setLength(protocol.getContent().length);
        ctx.writeAndFlush(protocol);
    }
}
