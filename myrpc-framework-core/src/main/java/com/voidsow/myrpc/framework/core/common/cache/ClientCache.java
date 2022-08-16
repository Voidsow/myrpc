package com.voidsow.myrpc.framework.core.common.cache;

import com.voidsow.myrpc.framework.core.common.Invocation;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ClientCache {
    static public Map<String, Invocation> RESP = new ConcurrentHashMap<>();
    static public BlockingQueue<Invocation> TASK_QUEUE = new ArrayBlockingQueue<>(100);
}
