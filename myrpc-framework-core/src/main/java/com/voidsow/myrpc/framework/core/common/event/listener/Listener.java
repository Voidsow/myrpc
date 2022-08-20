package com.voidsow.myrpc.framework.core.common.event.listener;

import com.voidsow.myrpc.framework.core.common.event.Event;

public interface Listener<T extends Event> {
    void callback(Event event);
}
