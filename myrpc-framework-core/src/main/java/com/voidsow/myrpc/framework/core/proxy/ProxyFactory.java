package com.voidsow.myrpc.framework.core.proxy;

import java.lang.reflect.Proxy;

public interface ProxyFactory {
    public <T> T getProxy(final Class<T> clazz);
}