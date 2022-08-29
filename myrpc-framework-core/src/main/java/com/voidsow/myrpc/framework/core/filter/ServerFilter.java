package com.voidsow.myrpc.framework.core.filter;

import com.voidsow.myrpc.framework.core.common.Invocation;
import com.voidsow.myrpc.framework.core.common.event.data.ChannelFutureWrapper;

import java.util.List;

public interface ServerFilter {
    boolean filter(Invocation invocation);
}
