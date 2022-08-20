package com.voidsow.myrpc.framework.core.common.event;

import com.voidsow.myrpc.framework.core.common.event.listener.Listener;
import com.voidsow.myrpc.framework.core.common.event.listener.ServiceUpdateListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 管理事件监听器的注册以及发送事件
 */
public class ListenerLoader {
    static List<Listener<?>> listeners = new ArrayList<>();

    static ExecutorService eventThreadPool = Executors.newFixedThreadPool(2);

    static void registerListener(Listener<?> listener) {
        listeners.add(listener);
    }

    public void init() {
        registerListener(new ServiceUpdateListener());
    }

    //获取listener的监听的event泛型类型
    static Class<?> getInterfaceParam(Listener<? extends Event> listener) {
        Type[] interfaces = listener.getClass().getGenericInterfaces();
        for (Type type : interfaces) {
            if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() == Listener.class) {
                return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
            }
        }
        return null;
    }

    public static void sendEvent(Event event) {
        if (!listeners.isEmpty()) {
            for (var listener : listeners) {
                Class<?> type = getInterfaceParam(listener);
                if (event.getClass().equals(type)) {
                    eventThreadPool.execute(() -> listener.callback(event));
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(getInterfaceParam(new ServiceUpdateListener()));
    }
}