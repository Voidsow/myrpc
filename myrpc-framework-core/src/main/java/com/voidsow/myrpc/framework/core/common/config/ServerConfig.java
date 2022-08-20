package com.voidsow.myrpc.framework.core.common.config;

public class ServerConfig {
    String application;
    int port;
    String registryAddr;

    public String getRegistryAddr() {
        return registryAddr;
    }

    public ServerConfig setRegistryAddr(String registryAddr) {
        this.registryAddr = registryAddr;
        return this;
    }

    public String getApplication() {
        return application;
    }

    public ServerConfig setApplication(String application) {
        this.application = application;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ServerConfig setPort(int port) {
        this.port = port;
        return this;
    }
}
