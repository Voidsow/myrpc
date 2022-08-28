package com.voidsow.myrpc.framework.core.rooter;

import com.voidsow.myrpc.framework.core.common.event.data.ChannelFutureWrapper;

import java.util.concurrent.atomic.AtomicInteger;

import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.SERVICE_ROUTER;

public class ChannelFutureRoller {
    AtomicInteger counter = new AtomicInteger(0);

    ChannelFutureWrapper getChannelFutureWrapper(String service) {
        ChannelFutureWrapper[] wrappers = SERVICE_ROUTER.get(service);
        int index = counter.getAcquire();
        return wrappers[index % wrappers.length];
    }
}
