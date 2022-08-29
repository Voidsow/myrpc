package com.voidsow.myrpc.framework.core.common;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Invocation {
    private String id;
    private String method;
    private String service;

    @Override
    public String toString() {
        return "Invocation{" +
                "id='" + id + '\'' +
                ", method='" + method + '\'' +
                ", service='" + service + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", response=" + response +
                '}';
    }

    private Object[] parameters;
    private Map<String, Object> attachments = new ConcurrentHashMap<>();
    private Object response;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, Object> attachments) {
        this.attachments = attachments;
    }
}
