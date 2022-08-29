package com.voidsow.myrpc.framework.core.filter;

import com.voidsow.myrpc.framework.core.common.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerLogFilter implements ServerFilter {
    static private final Logger logger = LoggerFactory.getLogger(ServerFilter.class);

    @Override
    public boolean filter(Invocation invocation) {
        logger.info("{} service handle request {} for invoke", invocation.getService(), invocation.getMethod());
        return true;
    }
}
