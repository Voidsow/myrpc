package com.voidsow.myrpc.framework.core.common.event.listener;

import com.voidsow.myrpc.framework.core.common.event.Event;
import com.voidsow.myrpc.framework.core.common.event.ProviderDataChangeEvent;
import com.voidsow.myrpc.framework.core.common.event.data.ChannelFutureWrapper;
import com.voidsow.myrpc.framework.core.registry.URL;
import com.voidsow.myrpc.framework.core.registry.zookeeper.ProviderNode;

import java.util.List;

import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.PROVIDERS;
import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.ROUTER;

public class ProviderDataChangeListener implements Listener<ProviderDataChangeEvent> {

    @Override
    public void callback(Event event) {
        ProviderNode node = ((ProviderDataChangeEvent) event).getProviderNode();
        List<ChannelFutureWrapper> channelFutureWrappers = PROVIDERS.get(node.getService());
        for (ChannelFutureWrapper wrapper : channelFutureWrappers) {
            if (node.getAddress().equals(wrapper.getHost() + ":" + wrapper.getPort())) {
                wrapper.setWeight(node.getWeight());
                URL url = new URL();
                url.setService(node.getService());
                ROUTER.updateWeight(url);
                break;
            }
        }
    }
}
