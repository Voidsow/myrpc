package com.voidsow.myrpc.framework.core.filter;

import com.voidsow.myrpc.framework.core.common.Invocation;
import com.voidsow.myrpc.framework.core.common.event.data.ChannelFutureWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ClientLogFilter implements ClientFilter {
    private static final Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);

    @Override
    public void filter(List<ChannelFutureWrapper> providers, Invocation invocation) {
        logger.info("invoke service:{}", invocation.getService());
    }
}
