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
import com.voidsow.myrpc.framework.core.rooter.Router;
import com.voidsow.myrpc.framework.core.serialize.SerializeFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Scanner;

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

    void initConfig() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> routerImplClazz = SPI.getService(Router.class, config.getRouterStrategy());
        ROUTER = (Router) routerImplClazz.getConstructor().newInstance();
        logger.info("environment:router strategy={}", config.getSerializer());
        Class<?> serializerImplClazz = SPI.getService(SerializeFactory.class, config.getSerializer());
        SERIALIZE_FACTORY = (SerializeFactory) serializerImplClazz.getConstructor().newInstance();
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
        //???????????????jdk????????????
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
     * ???????????????????????????????????????
     */
    public void connectServer() throws Exception {
        for (String service : SUBSCRIBE_SERVICES) {
            //?????????????????????provider?????????
            List<String> providerIps = abstractRegister.getProviderIps(service);
            for (String providerIp : providerIps) {
                //????????????????????????
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
                    //??????????????????????????????
                    Invocation invocation = TASK_QUEUE.take();
                    logger.info("????????????{}", invocation);
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
