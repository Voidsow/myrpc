package com.voidsow.myrpc.framework.core.filter;

import com.voidsow.myrpc.framework.core.common.Invocation;

import java.util.List;

public class ServerfilterChain {
    private List<ServerFilter> filters;

    public void addFilter(ServerFilter filter) {
        filters.add(filter);
    }

    public void doFilter(Invocation invocation) {
        for (ServerFilter filter : filters)
            if (!filter.filter(invocation)) break;
    }
}
