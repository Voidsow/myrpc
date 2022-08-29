package com.voidsow.myrpc.framework.core.server;

import com.voidsow.myrpc.framework.core.common.Decoder;
import com.voidsow.myrpc.framework.core.common.Encoder;
import com.voidsow.myrpc.framework.core.common.Utils;
import com.voidsow.myrpc.framework.core.common.config.ConfigLoader;
import com.voidsow.myrpc.framework.core.common.config.ServerConfig;
import com.voidsow.myrpc.framework.core.example.EchoServiceImpl;
import com.voidsow.myrpc.framework.core.registry.RegistryService;
import com.voidsow.myrpc.framework.core.registry.URL;
import com.voidsow.myrpc.framework.core.registry.zookeeper.ZookeeperRegister;
import com.voidsow.myrpc.framework.core.serialize.SerializeFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.voidsow.myrpc.framework.core.common.cache.ServerCache.*;

public class Server {
    Logger logger = LoggerFactory.getLogger(Server.class);

    ServerConfig config;

    RegistryService registryService;

    public Server(String configLocation) throws Exception {
        this.config = ConfigLoader.getServerConfig(configLocation);
        registryService = new ZookeeperRegister(config.getRegistryAddr());
        initConfig();
    }

    void initConfig() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> serializerImplClazz = SPI.getService(SerializeFactory.class, config.getSerializer());
        SERIALIZE_FACTORY = (SerializeFactory) serializerImplClazz.getConstructor().newInstance();
        logger.info("environment:serializer={}", config.getSerializer());
    }

    public void start() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new Decoder())
                                .addLast(new Encoder())
                                .addLast(new ServerHandler());
                    }
                });
        exposeService();
        bootstrap.bind(config.getPort()).sync();
    }

    public void exposeService() {
        for (URL url : PROVIDER_URLS)
            registryService.register(url);
    }

    public void registerService(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        //检查服务类是否为单接口
        if (interfaces.length == 0)
            throw new RuntimeException("service must be interface.");
        else if (interfaces.length > 1)
            throw new RuntimeException("service must be function single interface.");
        Class<?> serviceClazz = interfaces[0];

        PROVIDER_CLASS.put(serviceClazz.getName(), service);
        URL url = new URL();
        url.setService(serviceClazz.getName());
        url.setApplication(config.getApplication());
        url.addParameter("host", Utils.getIpAddress());
        url.addParameter("port", String.valueOf(config.getPort()));
        PROVIDER_URLS.add(url);
    }

    /**
     * @param args 输入一个参数，表示配置文件的位置
     */
    public static void main(String[] args) throws Exception {
        Server server = new Server(args.length > 0 ? args[0] : null);
        server.registerService(new EchoServiceImpl());
        server.start();
    }
}
