package com.voidsow.myrpc.framework.core.server;

import com.voidsow.myrpc.framework.core.common.Decoder;
import com.voidsow.myrpc.framework.core.common.Encoder;
import com.voidsow.myrpc.framework.core.example.EchoServiceImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import static com.voidsow.myrpc.framework.core.common.cache.ServerCache.PROVIDER_CLASS;

public class Server {
    ServerConfig config;

    public Server(ServerConfig config) {
        this.config = config;
    }

    public void start() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new Decoder())
                                .addLast(new Encoder())
                                .addLast(new ServerHandler());
                    }
                });
        bootstrap.bind(config.getPort()).sync();
    }

    public void registerService(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        //检查服务类是否为单接口
        if (interfaces.length == 0)
            throw new RuntimeException("service must be interface.");
        else if (interfaces.length > 1)
            throw new RuntimeException("service must be function single interface.");
        Class<?> serviceClazz = interfaces[0];
        //注册服务
        PROVIDER_CLASS.put(serviceClazz.getName(), service);
    }

    public static void main(String[] args) throws InterruptedException {
        ServerConfig config = new ServerConfig();
        config.setPort(1603);
        Server server = new Server(config);
        server.registerService(new EchoServiceImpl());
        server.start();
    }
}
