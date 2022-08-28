package com.voidsow.myrpc.framework.core.rooter;

import com.voidsow.myrpc.framework.core.common.event.data.ChannelFutureWrapper;
import com.voidsow.myrpc.framework.core.registry.URL;

public interface Router {
    /**
     * 刷新路由
     */
    void refreshRouter(Selector selector);

    /**
     * 返回请求对应的通道
     */
    ChannelFutureWrapper select(Selector selector);

    /**
     * 更新权重
     */
    void updateWeight(URL url);
}
