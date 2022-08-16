package com.voidsow.myrpc.framework.core.proxy.javassist;

import com.voidsow.myrpc.framework.core.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

public class JavassistProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, null);
    }
}
