package com.voidsow.myrpc.framework.core.registry.zookeeper;

import com.voidsow.myrpc.framework.core.common.event.Event;
import com.voidsow.myrpc.framework.core.common.event.ListenerLoader;
import com.voidsow.myrpc.framework.core.common.event.UpdateEvent;
import com.voidsow.myrpc.framework.core.common.event.data.UrlChange;
import com.voidsow.myrpc.framework.core.registry.AbstractRegister;
import com.voidsow.myrpc.framework.core.registry.RegistryService;
import com.voidsow.myrpc.framework.core.registry.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.PriorityQueue;

public class ZookeeperRegister extends AbstractRegister implements RegistryService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    AbstractZookeeperClient client;

    String ROOT = "/myrpc";

    /**
     * 获取url对应的provider路径，形如 /${ROOT}/${service}/provider/${host}:${port}
     */
    public String getProviderPath(URL url) {
        return ROOT + "/" + url.getService() + "/provider/" + url.getParameter("host") + ":" + url.getParameter("port");
    }

    /**
     * 获取url对应的consumer路径，形如/${ROOT}/${service}/consumer/${host}:
     */
    public String getConsumerPath(URL url) {
        return ROOT + "/" + url.getService() + "/consumer/" + url.getApplication() + ":" + url.getParameter("host") + ":";
    }

    public ZookeeperRegister(String address) throws Exception {
        client = new CuratorZookeeperClient(address);
        if (!client.exist(ROOT)) {
            client.createPersistentNode(ROOT, "");
        }
    }

    /**
     * 返回service的服务集群地址，形如[ip1:port1,ip2:port2,...]
     */
    @Override
    public List<String> getProviderIps(String service) throws Exception {
        return client.getChildren(ROOT + "/" + service + "/provider");
    }

    /**
     * 将服务器的地址登记到zookeeper
     */
    @Override
    public void register(URL url) {
        String urlStr = url.toProviderUrl();
        String path = getProviderPath(url);
        try {
            if (client.exist(path)) {
                client.setData(path, urlStr);
            } else
                client.createTemporaryNode(path, urlStr);
            super.register(url);
        } catch (Exception e) {
            logger.error("failed to register");
            e.printStackTrace();
        }
    }

    @Override
    public void unRegister(URL url) throws Exception {
        client.deleteNode(getProviderPath(url));
    }

    /**
     * 在订阅服务的consumer处登记
     */
    @Override
    public void subscribe(URL url) throws Exception {
        String path = getConsumerPath(url);
        //节点有序，若已经存在则必须删除
        if (client.exist(path))
            client.deleteNode(path);
        client.createTemporarySeqNode(path, url.toConsumerUrl());
        super.subscribe(url);
    }

    @Override
    public void unsubscribe(URL url) {
        client.deleteNode(getConsumerPath(url));
        super.unsubscribe(url);
    }

    @Override
    public void beforeSubscribe() {

    }

    @Override
    public void afterSubscribe(URL url) {
        String listenPath = ROOT + "/" + url.getService() + "/provider";
        watchChildNode(listenPath);
    }

    public void watchChildNode(String providerPath) {
        try {
            client.watchChildren(providerPath, event -> {
                String path = event.getPath();
                List<String> children = client.getChildren(path);
                UrlChange urlChange = new UrlChange(path.split("/")[2]);
                urlChange.setProviderUrl(children);
                //urlChange包含变更后的服务提供者ip，ip形如 host:port
                Event updateEvent = new UpdateEvent(urlChange);
                ListenerLoader.sendEvent(updateEvent);
                watchChildNode(path);
            });
        } catch (Exception e) {
            throw new ZookeeperException("failed to watch children update", e);
        }
    }

    double solution(double[] p, int[] a, int m) {
        PriorityQueue<Integer> queue = new PriorityQueue<>((e1, e2) -> (int) ((1 - p[e1]) * a[e1] - (1 - p[e2]) * a[e2]));
        double sum = 0;
        for (int i = 0; i < m; i++) {
            queue.add(i);
        }
        for (int i = m; i < a.length; i++) {
            queue.add(i);
            Integer index = queue.remove();
            sum += p[index] * a[index];
        }
        while (!queue.isEmpty()) {
            sum += a[queue.remove()];
        }
        return sum;
    }
}
