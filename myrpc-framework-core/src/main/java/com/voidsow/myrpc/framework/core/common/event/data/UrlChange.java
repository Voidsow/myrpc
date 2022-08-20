package com.voidsow.myrpc.framework.core.common.event.data;

import java.util.List;

public class UrlChange {
    String service;
    List<String> providerUrl;

    public UrlChange(String service) {
        this.service = service;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public List<String> getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(List<String> providerUrl) {
        this.providerUrl = providerUrl;
    }
}
