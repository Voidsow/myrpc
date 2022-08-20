package com.voidsow.myrpc.framework.core.common.cache;

import com.voidsow.myrpc.framework.core.registry.URL;
import io.netty.util.internal.ConcurrentSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class ServerCache {
    /**
     * 服务名对应的调用接口类
     */
    static public final Map<String, Object> PROVIDER_CLASS = new HashMap<>();
    /**
     * 暂时保存服务的URL，将URL的添加和登记到注册中心步骤分离
     */
    static public final Set<URL> PROVIDER_URLS = new HashSet<>();
}
