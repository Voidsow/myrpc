package com.voidsow.myrpc.framework.core.common.event;

import com.voidsow.myrpc.framework.core.registry.zookeeper.ProviderNode;

public class ProviderDataChangeEvent implements Event {
    ProviderNode providerNode;

    public ProviderDataChangeEvent(ProviderNode providerNode) {
        this.providerNode = providerNode;
    }

    public ProviderNode getProviderNode() {
        return providerNode;
    }
}
