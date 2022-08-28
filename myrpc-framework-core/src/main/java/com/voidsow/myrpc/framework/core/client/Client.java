package com.voidsow.myrpc.framework.core.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voidsow.myrpc.framework.core.common.*;
import com.voidsow.myrpc.framework.core.common.config.ClientConfig;
import com.voidsow.myrpc.framework.core.common.config.ConfigLoader;
import com.voidsow.myrpc.framework.core.common.event.ListenerLoader;
import com.voidsow.myrpc.framework.core.example.EchoService;
import com.voidsow.myrpc.framework.core.proxy.jdk.JDKProxyFactory;
import com.voidsow.myrpc.framework.core.registry.AbstractRegister;
import com.voidsow.myrpc.framework.core.registry.URL;
import com.voidsow.myrpc.framework.core.registry.zookeeper.ZookeeperRegister;
import com.voidsow.myrpc.framework.core.rooter.RandomRoterImpl;
import com.voidsow.myrpc.framework.core.rooter.RotateRouterImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static com.voidsow.myrpc.framework.core.common.Constant.ROUTER_TYPE_RANDOM;
import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.*;

public class Client {
    Logger logger = LoggerFactory.getLogger(Client.class);

    ObjectMapper mapper = Utils.getMapper();

    Bootstrap bootstrap;

    ClientConfig config;

    ListenerLoader listenerLoader;

    AbstractRegister abstractRegister;

    Client(String configLocation) throws Exception {
        config = ConfigLoader.getClientConfig(configLocation);
        abstractRegister = new ZookeeperRegister(config.getRegistryAddr());
        ROUTER = config.getRouterStrategy().equals(ROUTER_TYPE_RANDOM) ? new RandomRoterImpl() : new RotateRouterImpl();
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public RpcReference init() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new Encoder())
                                .addLast(new Decoder())
                                .addLast(new ClientHandler());
                    }
                });
        listenerLoader = new ListenerLoader();
        listenerLoader.init();
        RpcReference rpcReference;
        //目前仅支持jdk代理模式
//        if ("jdk".equals(config.getProxy()))
        rpcReference = new RpcReference(new JDKProxyFactory());
        return rpcReference;
    }

    public void start() throws Exception {
        ConnectionHandler.setBootstrap(bootstrap);
        connectServer();
        new Thread(new AsyncSend()).start();
    }

    public void subscribe(Class<?> serviceClass) throws Exception {
        URL url = new URL();
        url.setApplication(config.getApplication());
        url.setService(serviceClass.getName());
        url.addParameter("host", Utils.getIpAddress());
        abstractRegister.subscribe(url);
    }

    /**
     * 连接到所有已订阅的服务地址
     */
    public void connectServer() throws Exception {
        for (String service : SUBSCRIBE_SERVICES) {
            //从注册中心查询provider的地址
            List<String> providerIps = abstractRegister.getProviderIps(service);
            for (String providerIp : providerIps) {
                //连接到远程服务器
                ConnectionHandler.connect(service, providerIp);
            }
            URL url = new URL();
            url.setService(service);
            abstractRegister.afterSubscribe(url);
        }
    }

    class AsyncSend implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    //阻塞获取远程调用任务
                    Invocation invocation = TASK_QUEUE.take();
                    logger.info("发送请求{}", invocation);
                    String json = mapper.writeValueAsString(invocation);
                    Protocol protocol = new Protocol(json.getBytes());
                    ChannelFuture channelFuture = ConnectionHandler.getChannelFuture(invocation.getService());
                    channelFuture.channel().writeAndFlush(protocol);
                } catch (JsonProcessingException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client(args.length > 0 ? args[0] : null);
        client.subscribe(EchoService.class);
        RpcReference rpcReference = client.init();
        EchoService echo = rpcReference.get(EchoService.class);
        client.start();
        Scanner scanner = new Scanner(System.in);
        System.out.println(echo.send(scanner.nextLine()));
        int debug = 1;
    }

    class Pair {
        int index;
        int sum;
        int count;

        public Pair(int index, int sum) {
            this.index = index;
            this.sum = sum;
            count = 1;
        }
    }

    int solution(int[] nums) {
        int sum = 0;
        HashMap<Integer, Pair> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            Pair pair = map.get(nums[i]);
            if (pair == null) {
                map.put(nums[i], new Pair(i, 0));
            } else {
                //三元组中间的数量
                int count = 0;
                for (int j = pair.index + 1; j < i; j++) {
                    count += nums[j] > nums[i] ? 1 : 0;
                }
                pair.sum = pair.sum + pair.count * count;
                pair.count++;
                pair.index = i;
                sum += pair.sum;
            }
        }
        return sum;
    }
}
