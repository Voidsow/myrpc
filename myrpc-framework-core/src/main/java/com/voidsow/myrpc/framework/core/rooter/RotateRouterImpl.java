package com.voidsow.myrpc.framework.core.rooter;

import com.voidsow.myrpc.framework.core.common.event.data.ChannelFutureWrapper;
import com.voidsow.myrpc.framework.core.registry.URL;

import java.util.List;

import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.*;

public class RotateRouterImpl implements Router {
    @Override
    public void refreshRouter(Selector selector) {
        List<ChannelFutureWrapper> wrappers = PROVIDERS.get(selector.getService());
        SERVICE_ROUTER.put(selector.getService(), wrappers.toArray(new ChannelFutureWrapper[0]));
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return SERVICE_CHANNEL_ROLLER.getChannelFutureWrapper(selector.getService());
    }

    @Override
    public void updateWeight(URL url) {

    }
}
