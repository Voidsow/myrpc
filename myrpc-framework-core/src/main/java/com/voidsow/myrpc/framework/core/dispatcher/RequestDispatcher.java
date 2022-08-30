package com.voidsow.myrpc.framework.core.dispatcher;

import com.voidsow.myrpc.framework.core.common.Invocation;
import com.voidsow.myrpc.framework.core.common.Protocol;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.voidsow.myrpc.framework.core.common.cache.ServerCache.PROVIDER_CLASS;
import static com.voidsow.myrpc.framework.core.common.cache.ServerCache.SERIALIZE_FACTORY;

public class RequestDispatcher {
    static private final Logger logger = LoggerFactory.getLogger(ServiceHandler.class);
    private final BlockingQueue<Request> taskQueue;

    private final ExecutorService executors;

    public RequestDispatcher(int queueSize, int nThreads) {
        taskQueue = new ArrayBlockingQueue<>(queueSize);
        this.executors = Executors.newFixedThreadPool(nThreads);
        //启动handler
        new Thread(new ServiceHandler()).start();
    }

    public void addTask(Request task) {
        taskQueue.add(task);
    }

    class ServiceHandler implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Request task = taskQueue.take();
                    executors.submit(() -> {
                        try {
                            Protocol protocol = task.protocol;
                            Channel channel = task.context.channel();
                            Invocation invocation = SERIALIZE_FACTORY.deserialize(protocol.getContent(), Invocation.class);
                            logger.debug(String.valueOf(invocation));
                            logger.info("handle invoke[{}:{}] from {}",
                                    invocation.getService(), invocation.getMethod(), channel.remoteAddress());
                            //获取service对应的provider
                            Object service = PROVIDER_CLASS.get(invocation.getService());
                            if (service == null)
                                throw new RuntimeException("service not found");
                            Method[] methods = service.getClass().getDeclaredMethods();
                            Object result = null;
                            for (Method method : methods) {
                                if (method.getName().equals(invocation.getMethod())) {
                                    try {
                                        if (method.getReturnType().equals(Void.TYPE)) {
                                            method.invoke(service, invocation.getParameters());
                                        } else
                                            result = method.invoke(service, invocation.getParameters());
                                    } catch (InvocationTargetException e) {
                                        invocation.setError(e.getCause());
                                    }
                                    break;
                                }
                            }
                            invocation.setResponse(result);
                            protocol.setContent(SERIALIZE_FACTORY.serialize(invocation));
                            protocol.setLength(protocol.getContent().length);
                            task.context.writeAndFlush(protocol);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
