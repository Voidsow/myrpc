package com.voidsow.myrpc.framework.core.rooter;

import com.voidsow.myrpc.framework.core.common.event.data.ChannelFutureWrapper;
import com.voidsow.myrpc.framework.core.registry.URL;


import java.util.*;

import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.*;

public class RandomRoterImpl implements Router {
    @Override
    public void refreshRouter(Selector selector) {
        List<ChannelFutureWrapper> channelFutureWrappers = PROVIDERS.get(selector.getService());
        int[] randomSeq = new int[channelFutureWrappers.size()];
        ChannelFutureWrapper[] router = new ChannelFutureWrapper[randomSeq.length];
        Random random = new Random();
        boolean[] visited = new boolean[randomSeq.length];
        for (int i = 0; i < randomSeq.length; i++) {
            while (visited[randomSeq[i] = random.nextInt(randomSeq.length)]) ;
            router[i] = channelFutureWrappers.get(randomSeq[i]);
            visited[randomSeq[i]] = true;
        }
        SERVICE_ROUTER.put(selector.getService(), router);
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return SERVICE_CHANNEL_ROLLER.getChannelFutureWrapper(selector.getService());
    }

    @Override
    public void updateWeight(URL url) {
        List<ChannelFutureWrapper> channelFutureWrappers = PROVIDERS.get(url.getService());
        List<Integer> distribute = new ArrayList<>();
        //假定权重大小是100的整数倍
        //根据权重生成分布序列
        for (int i = 0; i < channelFutureWrappers.size(); i++) {
            ChannelFutureWrapper wrapper = channelFutureWrappers.get(i);
            for (int j = 0; j < wrapper.getWeight() / 100; j++)
                distribute.add(i);
        }
        ChannelFutureWrapper[] sample = new ChannelFutureWrapper[channelFutureWrappers.size()];
        //随机采样
        Random random = new Random();
        for (int i = 0; i < sample.length; i++) {
            sample[i] = channelFutureWrappers.get(random.nextInt(distribute.size()));
        }
        SERVICE_ROUTER.put(url.getService(), sample);
    }
}
