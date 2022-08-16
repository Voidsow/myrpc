package com.voidsow.myrpc.framework.core.client;

public class ClientConfig {
    String addr;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    int port;
}
