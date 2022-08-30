package com.voidsow.myrpc.framework.core.common.config;

public class ServerConfig {
    String application;
    int port;
    String registryAddr;
    String serializer;
    int queueSize;
    int handlerNum;

    public int getHandlerNum() {
        return handlerNum;
    }

    public ServerConfig setHandlerNum(int handlerNum) {
        this.handlerNum = handlerNum;
        return this;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public ServerConfig setQueueSize(int queueSize) {
        this.queueSize = queueSize;
        return this;
    }

    public String getSerializer() {
        return serializer;
    }

    public ServerConfig setSerializer(String serializer) {
        this.serializer = serializer;
        return this;
    }

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
