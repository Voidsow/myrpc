package com.voidsow.myrpc.framework.core.filter;

import com.voidsow.myrpc.framework.core.common.Invocation;
import com.voidsow.myrpc.framework.core.common.event.data.ChannelFutureWrapper;

import java.util.List;

public class ClientFilterChain {
    private List<ClientFilter> filters;

    public void addFilter(ClientFilter filter) {
        filters.add(filter);
    }

    public void doFilter(List<ChannelFutureWrapper> wrappers, Invocation invocation) {
        filters.forEach(filter -> filter.filter(wrappers, invocation));
    }
}
