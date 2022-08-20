package com.voidsow.myrpc.framework.core.registry.zookeeper;

import org.apache.zookeeper.Watcher;

import java.util.List;

public abstract class AbstractZookeeperClient {
    String address;
    int baseSleepTime;
    int maxRetryTimes;

    public AbstractZookeeperClient(String address) {
        this(address, null, null);
    }

    public AbstractZookeeperClient(String address, Integer baseSleepTime, Integer maxRetryTimes) {
        this.address = address;
        this.baseSleepTime = baseSleepTime == null ? 1000 : baseSleepTime;
        this.maxRetryTimes = maxRetryTimes == null ? 3 : maxRetryTimes;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBaseSleepTime() {
        return baseSleepTime;
    }

    public void setBaseSleepTime(int baseSleepTime) {
        this.baseSleepTime = baseSleepTime;
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public abstract Object getClient();

    public abstract void updateNodeData(String path,String data);

    public abstract String getNodeData(String path);

    /**
     * 获取子节点的数据
     *
     * @param path 父节点路径
     */
    public abstract List<String> getChildren(String path);

    public abstract void createPersistentNode(String path, String data);

    public abstract void createPersistentSeqNode(String path, String data);

    public abstract void createTemporaryNode(String path, String data);

    public abstract void createTemporarySeqNode(String path, String data);

    public abstract void setData(String path, String data);

    public abstract void close() throws Exception;

//    public abstract List<String> getChildrenData(String path);

    public abstract boolean deleteNode(String path);

    public abstract boolean exist(String path);

    public abstract void watchNodeData(String path, Watcher watcher);

    public abstract void watchChildren(String path, Watcher watcher);
}
