package com.voidsow.myrpc.framework.core.registry;

import java.util.List;

import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.SUBSCRIBE_SERVICES;
import static com.voidsow.myrpc.framework.core.common.cache.ServerCache.PROVIDER_URLS;

public abstract class AbstractRegister implements RegistryService {
    @Override
    public void register(URL url) {
        PROVIDER_URLS.add(url);
    }

    @Override
    public void unRegister(URL url) throws Exception {
        PROVIDER_URLS.remove(url);
    }

    @Override
    public void subscribe(URL url) throws Exception {
        SUBSCRIBE_SERVICES.add(url.getService());
    }

    @Override
    public void unsubscribe(URL url) {
        SUBSCRIBE_SERVICES.remove(url.getService());
    }

    //子类实现
    public abstract void beforeSubscribe() throws Exception;

    public abstract void afterSubscribe(URL url) throws Exception;

    /**
     * 返回service的provider集群的地址，形如 [ip1:port1,ip2:port2,...]
     */
    public abstract List<String> getProviderIps(String service) throws Exception;
}
