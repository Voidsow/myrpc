package com.voidsow.myrpc.framework.core.dispatcher;

import com.voidsow.myrpc.framework.core.common.Protocol;
import io.netty.channel.ChannelHandlerContext;

public class Request {
    Protocol protocol;

    ChannelHandlerContext context;

    public Request(Protocol protocol, ChannelHandlerContext context) {
        this.protocol = protocol;
        this.context = context;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }
}
