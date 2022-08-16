package com.voidsow.myrpc.framework.core.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voidsow.myrpc.framework.core.common.*;
import com.voidsow.myrpc.framework.core.example.EchoService;
import com.voidsow.myrpc.framework.core.proxy.jdk.JDKProxyFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.voidsow.myrpc.framework.core.common.cache.ClientCache.TASK_QUEUE;

public class Client {
    Logger logger = LoggerFactory.getLogger(Client.class);

    ObjectMapper mapper = Utils.getMapper();

    ClientConfig config;

    Client(ClientConfig config) {
        this.config = config;
    }

    public RpcReference start() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
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
        ChannelFuture future = bootstrap.connect(config.getAddr(), config.getPort()).sync();
        logger.info("连接到远程服务");
        new Thread(new SendJob(future)).start();
        return new RpcReference(new JDKProxyFactory());
    }

    class SendJob implements Runnable {

        ChannelFuture channelFuture;

        public SendJob(ChannelFuture channelFuture) {
            this.channelFuture = channelFuture;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    //阻塞获取远程调用任务
                    Invocation invocation = TASK_QUEUE.take();
                    logger.info("发送请求");
                    String json = mapper.writeValueAsString(invocation);
                    Protocol protocol = new Protocol(json.getBytes());
                    channelFuture.channel().writeAndFlush(protocol);
                } catch (JsonProcessingException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ClientConfig config = new ClientConfig();
        config.setAddr("localhost");
        config.setPort(1603);
        Client client = new Client(config);
        RpcReference rpcReference = client.start();
        EchoService echo = rpcReference.get(EchoService.class);
        System.out.println(echo.send("test"));
    }
}
