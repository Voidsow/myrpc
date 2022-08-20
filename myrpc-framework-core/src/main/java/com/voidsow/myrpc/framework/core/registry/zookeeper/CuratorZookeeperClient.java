package com.voidsow.myrpc.framework.core.registry.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;

import java.util.List;

public class CuratorZookeeperClient extends AbstractZookeeperClient {

    CuratorFramework client;

    public CuratorZookeeperClient(String address) {
        this(address, null, null);
    }

    public CuratorZookeeperClient(String address, Integer baseSleepTime, Integer maxRetryTimes) {
        super(address, baseSleepTime, maxRetryTimes);
        client = CuratorFrameworkFactory.newClient(address,
                new ExponentialBackoffRetry(super.getBaseSleepTime(), super.getMaxRetryTimes()));
        client.start();
    }

    @Override
    public Object getClient() {
        return client;
    }

    @Override
    public void updateNodeData(String path, String data) {
        try {
            client.setData().forPath(path, data.getBytes());
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    @Override
    public String getNodeData(String path) {
        byte[] bytes;
        try {
            bytes = client.getData().forPath(path);
            return bytes != null ? new String(bytes) : null;
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    @Override
    public List<String> getChildren(String path) {
        try {
            return client.getChildren().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            return null;
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    @Override
    public void createPersistentNode(String path, String data) {
        try {
            client.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, data.getBytes());
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    @Override
    public void createPersistentSeqNode(String path, String data) {
        try {
            client.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(path, data.getBytes());
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    @Override
    public void createTemporaryNode(String path, String data) {
        try {
            client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, data.getBytes());
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    @Override
    public void createTemporarySeqNode(String path, String data) {
        try {
            client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, data.getBytes());
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    @Override
    public void setData(String path, String data) {
        try {
            client.setData().forPath(path, data.getBytes());
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    @Override
    public void close() {
        client.close();
    }


    @Override
    public boolean deleteNode(String path) {
        try {
            client.delete().forPath(path);
            return true;
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    @Override
    public boolean exist(String path) {
        try {
            return client.checkExists().forPath(path) != null;
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    @Override
    public void watchNodeData(String path, Watcher watcher) {
        try {
            client.getData().usingWatcher(watcher).forPath(path);
        } catch (Exception e) {
            throw new ZookeeperException(e);
        }
    }

    @Override
    public void watchChildren(String path, Watcher watcher) {
        try {
            client.getChildren().usingWatcher(watcher).forPath(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
