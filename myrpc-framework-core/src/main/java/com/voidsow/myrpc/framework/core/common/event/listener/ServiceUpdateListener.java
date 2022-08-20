package com.voidsow.myrpc.framework.core.common.event.listener;

import com.voidsow.myrpc.framework.core.client.ConnectionHandler;
import com.voidsow.myrpc.framework.core.common.Utils;
import com.voidsow.myrpc.framework.core.common.event.Event;
import com.voidsow.myrpc.framework.core.common.event.UpdateEvent;
import com.voidsow.myrpc.framework.core.common.event.data.ChannelFutureWrapper;
import com.voidsow.myrpc.framework.core.common.event.data.UrlChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.PROVIDERS;

public class ServiceUpdateListener implements Listener<UpdateEvent> {
    Logger logger = LoggerFactory.getLogger(ServiceUpdateListener.class);

    @Override
    public void callback(Event event) {
        UrlChange urlChange = ((UpdateEvent) event).getUrlChange();
        logger.info("提供{}服务的节点发生变化，现在提供服务的节点为{}", urlChange.getService(), urlChange.getProviderUrl());
        List<ChannelFutureWrapper> channelFutureWrappers = PROVIDERS.get(urlChange.getService());
        if (Utils.isEmpty(channelFutureWrappers)) {
            logger.error("updateEvent is empty");
            return;
        }
        List<String> updatedUrl = urlChange.getProviderUrl();
        Set<String> newConnectingUrl = new HashSet<>();
        List<ChannelFutureWrapper> newWrappers = new ArrayList<>();
        //移除下线的服务提供者，保留依然在线的
        for (ChannelFutureWrapper wrapper : channelFutureWrappers) {
            String alive = wrapper.getHost() + ":" + wrapper.getPort();
            if (updatedUrl.contains(alive)) {
                newWrappers.add(wrapper);
                newConnectingUrl.add(alive);
            }
        }
        //添加新增的服务提供者
        for (String newUrl : updatedUrl) {
            if (!newConnectingUrl.contains(newUrl)) {
                String[] split = newUrl.split(":");
                String ip = split[0];
                int port = Integer.parseInt(split[1]);
                ChannelFutureWrapper wrapper = new ChannelFutureWrapper(ip, port, null);
                try {
                    wrapper.setChannelFuture(ConnectionHandler.createChannelFuture(ip, port));
                    newWrappers.add(wrapper);
                    //此处的添加可以优化掉
                    newConnectingUrl.add(newUrl);
                } catch (InterruptedException e) {
                    logger.warn("failed to connect to the server {}", newUrl);
                }
            }
        }
        PROVIDERS.put(urlChange.getService(), newWrappers);
    }
}
