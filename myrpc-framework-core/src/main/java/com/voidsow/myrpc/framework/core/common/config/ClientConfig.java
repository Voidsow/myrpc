package com.voidsow.myrpc.framework.core.common.config;

public class ClientConfig {
    String application;

    String registryAddr;

    String proxy;

    String routerStrategy;

    String serializer;

    public String getSerializer() {
        return serializer;
    }

    public ClientConfig setSerializer(String serializer) {
        this.serializer = serializer;
        return this;
    }

    public String getRouterStrategy() {
        return routerStrategy;
    }

    public ClientConfig setRouterStrategy(String routerStrategy) {
        this.routerStrategy = routerStrategy;
        return this;
    }

    public String getApplication() {
        return application;
    }

    public ClientConfig setApplication(String application) {
        this.application = application;
        return this;
    }

    public String getRegistryAddr() {
        return registryAddr;
    }

    public ClientConfig setRegistryAddr(String registryAddr) {
        this.registryAddr = registryAddr;
        return this;
    }

    public String getProxy() {
        return proxy;
    }

    public ClientConfig setProxy(String proxy) {
        this.proxy = proxy;
        return this;
    }
}
