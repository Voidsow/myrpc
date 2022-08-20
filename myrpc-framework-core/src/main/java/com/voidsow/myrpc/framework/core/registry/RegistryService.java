package com.voidsow.myrpc.framework.core.registry;

public interface RegistryService {
    void register(URL url);

    void unRegister(URL url) throws Exception;

    void subscribe(URL url) throws Exception;

    void unsubscribe(URL url);
}
