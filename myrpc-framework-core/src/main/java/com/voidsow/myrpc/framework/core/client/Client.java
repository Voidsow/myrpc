package com.voidsow.myrpc.framework.core.client;

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
import com.voidsow.myrpc.framework.core.serialize.HessianSerializeFactory;
import com.voidsow.myrpc.framework.core.serialize.JdkSerializeFactory;
import com.voidsow.myrpc.framework.core.serialize.JsonSerializeFactory;
import com.voidsow.myrpc.framework.core.serialize.KryoSerializeFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jboss.netty.buffer.DirectChannelBufferFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

import static com.voidsow.myrpc.framework.core.common.Constant.*;
import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.*;

public class Client {
    Logger logger = LoggerFactory.getLogger(Client.class);

    Bootstrap bootstrap;

    ClientConfig config;

    ListenerLoader listenerLoader;

    AbstractRegister abstractRegister;

    Client(String configLocation) throws Exception {
        config = ConfigLoader.getClientConfig(configLocation);
        abstractRegister = new ZookeeperRegister(config.getRegistryAddr());
        initConfig();
    }

    void initConfig() {
        ROUTER = config.getRouterStrategy().equals(ROUTER_TYPE_RANDOM) ? new RandomRoterImpl() : new RotateRouterImpl();
        switch (config.getSerializer()) {
            case SERIALIZE_TYPE_JDK:
                SERIALIZE_FACTORY = new JdkSerializeFactory();
                break;
            case SERIALIZE_TYPE_HESSIAN:
                SERIALIZE_FACTORY = new HessianSerializeFactory();
                break;
            case SERIALIZE_TYPE_KRYO:
                SERIALIZE_FACTORY = new KryoSerializeFactory();
                break;
            case SERIALIZE_TYPE_JSON:
                SERIALIZE_FACTORY = new JsonSerializeFactory();
                break;
            default:
                throw new RuntimeException(String.format("no match serializer for %s", config.getSerializer()));
        }
        logger.info("environment:serializer={}", config.getSerializer());
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
        ByteBuf buffer;

    }

    class AsyncSend implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    //阻塞获取远程调用任务
                    Invocation invocation = TASK_QUEUE.take();
                    logger.info("发送请求{}", invocation);
                    Protocol protocol = new Protocol(SERIALIZE_FACTORY.serialize(invocation));
                    ChannelFuture channelFuture = ConnectionHandler.getChannelFuture(invocation.getService());
                    channelFuture.channel().writeAndFlush(protocol);
                } catch (InterruptedException e) {
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
        while (true) {
            System.out.println(echo.send(scanner.nextLine()));
        }
    }
}
