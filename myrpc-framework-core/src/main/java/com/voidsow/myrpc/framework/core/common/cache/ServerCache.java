package com.voidsow.myrpc.framework.core.common.cache;

import com.voidsow.myrpc.framework.core.registry.URL;
import com.voidsow.myrpc.framework.core.serialize.SerializeFactory;
import com.voidsow.myrpc.framework.core.spi.ServiceLoader;

import java.util.*;

public class ServerCache {
    /**
     * 服务名对应的调用接口类
     */
    static public final Map<String, Object> PROVIDER_CLASS = new HashMap<>();
    /**
     * 暂时保存服务的URL，将URL的添加和登记到注册中心步骤分离
     */
    static public final Set<URL> PROVIDER_URLS = new HashSet<>();

    /**
     * 序列化工具类
     */
    static public SerializeFactory SERIALIZE_FACTORY;

    /**
     * SPI
     */
    static public ServiceLoader SPI = new ServiceLoader();
}
