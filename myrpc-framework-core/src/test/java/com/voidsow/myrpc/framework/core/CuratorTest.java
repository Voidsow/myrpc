package com.voidsow.myrpc.framework.core;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorTest {

    CuratorFramework client;

    public CuratorFramework getConnection() {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .namespace("rpc")
                .connectString("localhost:2181")
                .retryPolicy(new ExponentialBackoffRetry(3000, 10)).build();
        client.start();
        return client;
    }

    //连接
    @Before
    public void connect() {
        client = getConnection();
    }

    @After
    public void close() {
        client.close();
    }

    //创建结点
    @Test
    public void create() throws Exception {
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                .forPath("/app4", "".getBytes());
    }

    //查询数据
    @Test
    public void get() throws Exception {
        Stat stat = new Stat();
        byte[] bytes = client.getData().storingStatIn(stat).forPath("/app1");
        System.out.println(new String(bytes));
        System.out.println(stat);
    }

    //修改数据
    @Test
    public void setByVersion() throws Exception {
        Stat stat = new Stat();
        String path = "/app1";
        client.getData().storingStatIn(stat).forPath(path);
        int version = stat.getVersion();
        System.out.println(version);
        client.setData().withVersion(stat.getVersion()).forPath(path, String.format("set app1 version%d", version).getBytes());
    }

    //删除数据
    @Test
    public void deleteR() throws Exception {
        client.delete().deletingChildrenIfNeeded().forPath("/app2");
//        client.delete().guaranteed().deletingChildrenIfNeeded().
//                inBackground((client1, event) -> System.out.println(event)).forPath("/app2");
    }

    //监听单一结点
    @Test
    public void watch() throws Exception {
        String path = "/app2";
        NodeCache nodeCache = new NodeCache(client, path);
        nodeCache.getListenable().addListener(
                () -> System.out.printf("结点%s的数据发生了变化，当前数据为%s%n", path, new String(nodeCache.getCurrentData().getData())));
        nodeCache.start(true);
    }

    //监听子结点
    @Test
    public void watchChildren() throws Exception {
        String path = "/app2";
        client.getChildren().usingWatcher((Watcher) event -> {
            System.out.println(event.getPath());
            System.out.println(event.getType());
            System.out.println("-------------------------------------");
        }).forPath(path);
        while (true) {

        }
    }

    //监听整棵树
    @Test
    public void watchWhole() throws Exception {
        String path = "/app2";
        TreeCache cache = new TreeCache(client, "/app2");
        cache.getListenable().addListener(
                new TreeCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                        System.out.printf("结点%s的数据发生了变化，当前数据为%s%n", event.getData().getPath(), new String(event.getData().getData()));
                    }
                });
        cache.start();
    }
}
