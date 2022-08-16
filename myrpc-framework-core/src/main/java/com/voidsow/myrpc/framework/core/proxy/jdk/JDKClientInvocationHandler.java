package com.voidsow.myrpc.framework.core.proxy.jdk;

import com.voidsow.myrpc.framework.core.common.Invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

import static com.voidsow.myrpc.framework.core.common.Constant.TIME_OUT_MILLIS;
import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.RESP;
import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.TASK_QUEUE;

public class JDKClientInvocationHandler implements InvocationHandler {
    Class<?> serviceClazz;

    public JDKClientInvocationHandler(Class<?> serviceClazz) {
        this.serviceClazz = serviceClazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Invocation invocation = new Invocation();
        invocation.setParameters(args);
        invocation.setMethod(method.getName());
        invocation.setService(serviceClazz.getName());
        String id = UUID.randomUUID().toString();
        invocation.setId(id);
        RESP.put(id, invocation);
        TASK_QUEUE.add(invocation);
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < TIME_OUT_MILLIS) {
            Invocation resp = RESP.get(id);
            if (resp.getResponse() != null) {
                return resp.getResponse();
            }
        }
        throw new RuntimeException("request timeout");
    }
}