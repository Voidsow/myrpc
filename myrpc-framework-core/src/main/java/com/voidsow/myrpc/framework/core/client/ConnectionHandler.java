package com.voidsow.myrpc.framework.core.client;

import com.voidsow.myrpc.framework.core.common.Utils;
import com.voidsow.myrpc.framework.core.common.event.data.ChannelFutureWrapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.PROVIDERS;
import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.SERVER_ADDRESS;

/**
 * 负责客户端与服务端建立连接，注册中心的服务结点发生变动时，建立或断开对应的连接
 */
public class ConnectionHandler {
    static Bootstrap bootstrap;

    public static void setBootstrap(Bootstrap bootstrap) {
        ConnectionHandler.bootstrap = bootstrap;
    }

    /**
     * 连接到地址对应的服务端，并统一登记到cache，用于客户端初始化订阅服务
     *
     * @param service    服务名
     * @param providerIp 将要建立连接的地址，形如ip:port
     */
    public static void connect(String service, String providerIp) throws InterruptedException {
        if (bootstrap == null)
            throw new RuntimeException("bootstrap not initialized");
        if (providerIp.indexOf(':') == -1)
            throw new RuntimeException("incorrect address format");
        String[] providerAddress = providerIp.split(":");
        String ip = providerAddress[0];
        int port = Integer.parseInt(providerAddress[1]);
        ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
        ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper(ip, port, null);
        channelFutureWrapper.setChannelFuture(channelFuture);
        SERVER_ADDRESS.add(providerIp);
        List<ChannelFutureWrapper> channelFutureWrappers = PROVIDERS.compute(service, (k, v) -> v == null ? new ArrayList<>() : v);
        channelFutureWrappers.add(channelFutureWrapper);
    }

    /**
     * 与给定地址建立连接，返回ChannelFuture
     */
    public static ChannelFuture createChannelFuture(String ip, Integer port) throws InterruptedException {
        return bootstrap.connect(ip, port).sync();
    }

    //服务商下线，不再保存provide的地址
    public static void disconnect(String service, String providerIp) {
        SERVER_ADDRESS.remove(providerIp);
        List<ChannelFutureWrapper> providers = PROVIDERS.get(service);
        if (!Utils.isEmpty(providers)) {
            Iterator<ChannelFutureWrapper> it = providers.iterator();
            while (it.hasNext()) {
                ChannelFutureWrapper provider = it.next();
                if (providerIp.equals(provider.getHost() + ":" + provider.getPort())) {
                    it.remove();
                    break;
                }
            }
        }
    }

    /**
     * 随机返回该服务集群其中一个的连接
     */
    public static ChannelFuture getChannelFuture(String service) {
        List<ChannelFutureWrapper> channelFutureWrappers = PROVIDERS.get(service);
        if (Utils.isEmpty(channelFutureWrappers))
            throw new RuntimeException("not found provider for " + service);
        return channelFutureWrappers.get(new Random().nextInt(channelFutureWrappers.size())).getChannelFuture();
    }
}
