package com.voidsow.myrpc.framework.core.common.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    static private final String FILE_LOCATION = "myrpc.properties";

    //property的key
    public static final String SERVER_PORT = "myrpc.server.port";
    public static final String REGISTER_ADDRESS = "myrpc.registry.address";
    public static final String APPLICATION_NAME = "myrpc.application";
    public static final String PROXY_TYPE = "myrpc.proxy";
    public static final String ROUTER_TYPE = "myrpc.router.strategy";
    public static final String SERIALIZE_TYPE = "myrpc.serialize.type";
    public static final String REQUEST_QUEUE_SZIE = "myrpc.server.queue.size";
    public static final String REQUEST_HANDLER_NUM = "myrpc.server.handler.num";


    public static Properties loadConfiguration(String location) throws IOException {
        Properties properties = new Properties();
        if (location == null)
            location = FILE_LOCATION;
        FileInputStream inputStream = new FileInputStream(location);
        properties.load(inputStream);
        return properties;
    }

    public static ServerConfig getServerConfig(String location) throws IOException {
        Properties properties = loadConfiguration(location);
        ServerConfig config = new ServerConfig();
        return config.setApplication(properties.getProperty(APPLICATION_NAME)).
                setPort(Integer.parseInt(properties.getProperty(SERVER_PORT))).
                setRegistryAddr(properties.getProperty(REGISTER_ADDRESS))
                .setSerializer(properties.getProperty(SERIALIZE_TYPE))
                .setQueueSize(Integer.parseInt(properties.getProperty(REQUEST_QUEUE_SZIE)))
                .setHandlerNum(Integer.parseInt(properties.getProperty(REQUEST_HANDLER_NUM)));
    }

    public static ClientConfig getClientConfig(String location) throws IOException {
        Properties properties = loadConfiguration(location);
        ClientConfig config = new ClientConfig();
        return config.setApplication(properties.getProperty(APPLICATION_NAME))
                .setProxy(properties.getProperty(PROXY_TYPE))
                .setRegistryAddr(properties.getProperty(REGISTER_ADDRESS))
                .setRouterStrategy(properties.getProperty(ROUTER_TYPE))
                .setSerializer(properties.getProperty(SERIALIZE_TYPE));
    }
}
