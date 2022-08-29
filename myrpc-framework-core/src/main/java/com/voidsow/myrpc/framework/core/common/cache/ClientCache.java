package com.voidsow.myrpc.framework.core.common.cache;

import com.voidsow.myrpc.framework.core.common.Invocation;
import com.voidsow.myrpc.framework.core.common.event.data.ChannelFutureWrapper;
import com.voidsow.myrpc.framework.core.registry.URL;
import com.voidsow.myrpc.framework.core.rooter.ChannelFutureRoller;
import com.voidsow.myrpc.framework.core.rooter.Router;
import com.voidsow.myrpc.framework.core.serialize.SerializeFactory;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ClientCache {
    /**
     * 远程调用响应结果
     */
    static public Map<String, Invocation> RESP = new ConcurrentHashMap<>();
    /**
     * 待发送调用队列
     */
    static public BlockingQueue<Invocation> TASK_QUEUE = new ArrayBlockingQueue<>(100);
    /**
     * 已订阅的服务
     */
    static public List<String> SUBSCRIBE_SERVICES = new ArrayList<>();
    /**
     * 服务对应的集群URL，形如 service->[URL1,URL2,...]
     */
    static public Map<String, List<URL>> URL_MAP = new ConcurrentHashMap<>();

    /**
     * 已建立连接的远程服务的url，形如 ip:port
     */
    static public Set<String> SERVER_ADDRESS = new HashSet<>();

    /**
     * 实际调用时的服务提供者对应的channel
     */
    static public Map<String, List<ChannelFutureWrapper>> PROVIDERS = new ConcurrentHashMap<>();

    /**
     * 随机路由数组
     */
    static public Map<String, ChannelFutureWrapper[]> SERVICE_ROUTER = new ConcurrentHashMap<>();

    static public ChannelFutureRoller SERVICE_CHANNEL_ROLLER = new ChannelFutureRoller();

    /**
     * 请求分发路由
     */
    static public Router ROUTER;

    /**
     * 序列化工具类
     */
    static public SerializeFactory SERIALIZE_FACTORY;
}
