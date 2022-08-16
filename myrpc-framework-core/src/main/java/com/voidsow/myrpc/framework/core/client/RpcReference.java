package com.voidsow.myrpc.framework.core.client;

import com.voidsow.myrpc.framework.core.proxy.ProxyFactory;

public class RpcReference {
    ProxyFactory proxyFactory;

    public RpcReference(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public <T> T get(Class<T> clazz) {
        return proxyFactory.getProxy(clazz);
    }
}
