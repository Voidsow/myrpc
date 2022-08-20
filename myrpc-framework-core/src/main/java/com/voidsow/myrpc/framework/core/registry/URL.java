package com.voidsow.myrpc.framework.core.registry;

import com.voidsow.myrpc.framework.core.registry.zookeeper.ProviderNode;

import java.util.HashMap;
import java.util.Map;

public class URL {
    String application;
    String service;
    Map<String, String> parameters = new HashMap<>();

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameter(String param) {
        return parameters.get(param);
    }

    public URL addParameter(String param, String value) {
        parameters.put(param, value);
        return this;
    }

    /**
     * 将URL转换为provider子结点的data，形如 appName;serviceName;host:port;timestamp
     */
    public String toProviderUrl() {
        return getApplication() + ";" + getService() + ";" +
                parameters.get("host") + ":" + parameters.get("port") + ";" + System.currentTimeMillis();
    }

    /**
     * 将URL转换为consumer子结点的data，形如 appName;serviceName;host;timestamp
     */
    public String toConsumerUrl() {
        return getApplication() + ";" + getService() + ";" +
                parameters.get("host") + ";" + System.currentTimeMillis();
    }

    /**
     * 将节点的路径转换为ProviderNode
     *
     * @param path provider的路径
     */
    public static ProviderNode url2provider(String path) {
        String[] split = path.split("/");
        ProviderNode providerNode = new ProviderNode();
        providerNode.setService(split[2]);
        providerNode.setAddress(split[4]);
        return providerNode;
    }

    public static void main(String[] args) {
        URL url = new URL();
        url.setApplication("online-shopping");
        url.setService("search");
        url.addParameter("host", "localhost")
                .addParameter("port", "1603");
        System.out.println(url.toProviderUrl());
        System.out.println(url.toConsumerUrl());
    }
}
