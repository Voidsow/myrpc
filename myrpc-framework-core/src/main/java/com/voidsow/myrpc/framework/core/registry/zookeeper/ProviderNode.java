package com.voidsow.myrpc.framework.core.registry.zookeeper;

public class ProviderNode {
    String service;

    String address;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "ProviderNode{" +
                "service='" + service + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
