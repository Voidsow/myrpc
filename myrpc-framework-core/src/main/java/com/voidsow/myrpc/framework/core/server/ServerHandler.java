package com.voidsow.myrpc.framework.core.server;

import com.voidsow.myrpc.framework.core.common.Handler;
import com.voidsow.myrpc.framework.core.common.Invocation;
import com.voidsow.myrpc.framework.core.common.Protocol;
import com.voidsow.myrpc.framework.core.dispatcher.Request;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static com.voidsow.myrpc.framework.core.common.cache.ServerCache.*;

public class ServerHandler extends Handler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DISPATCHER.addTask(new Request((Protocol) msg, ctx));
    }
}
