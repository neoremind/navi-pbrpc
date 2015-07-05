package com.baidu.beidou.navi.pbrpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.server.core.ServiceLocator;
import com.baidu.beidou.navi.pbrpc.server.core.impl.IdKeyServiceLocator;
import com.baidu.beidou.navi.pbrpc.server.handler.PbrpcServerHandler;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMessageDeserializer;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMessageSerializer;
import com.baidu.beidou.navi.pbrpc.transport.handler.RpcServerChannelIdleHandler;

/**
 * ClassName: PbrpcServer <br/>
 * Function: 基于netty nio的Pbrpc服务端
 * 
 * @author Zhang Xu
 */
public class PbrpcServer {

    private static final Logger LOG = LoggerFactory.getLogger(PbrpcServer.class);

    /**
     * 服务端口
     */
    private int port;

    /**
     * netty的服务启动对象
     */
    private ServerBootstrap bootstrap;

    /**
     * 接受客户端请求的线程
     */
    private EventLoopGroup bossGroup;

    /**
     * 处理业务逻辑的线程
     */
    private EventLoopGroup workerGroup;

    /**
     * 服务端内部服务的路由定位器
     */
    private ServiceLocator<Integer> serviceLocator = new IdKeyServiceLocator();

    /**
     * 服务端配置
     */
    private PbrpcServerConfiguration pbrpcServerConfiguration = new PbrpcServerConfiguration();

    /**
     * 由{@link IdleStateHandler}使用的构造参数，用于检查channel是否长时间没有写入或者读取，关闭之以节省服务端资源<br/>
     * 此处
     * 
     * <pre>
     * int readerIdleTimeSeconds,
     * int writerIdleTimeSeconds,
     *  int allIdleTimeSeconds
     * </pre>
     * 
     * 简单处理，三者共用 <br/>
     * 单位为秒。
     */
    private static final int IDLE_CHANNEL_TIMEOUT = 3600;

    /**
     * Creates a new instance of PbrpcServer.
     * 
     * @param port
     */
    public PbrpcServer(int port) {
        this(null, port);
    }

    /**
     * Creates a new instance of PbrpcServer.
     * 
     * @param pbrpcServerConfiguration
     * @param port
     */
    public PbrpcServer(PbrpcServerConfiguration pbrpcServerConfiguration, int port) {
        // use default conf otherwise use specified one
        if (pbrpcServerConfiguration == null) {
            pbrpcServerConfiguration = this.pbrpcServerConfiguration;
        }
        this.port = port;

        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, pbrpcServerConfiguration.getSoBacklog());

        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, pbrpcServerConfiguration.isSoKeepalive());
        bootstrap.childOption(ChannelOption.TCP_NODELAY, pbrpcServerConfiguration.isTcpNodelay());
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.childOption(ChannelOption.SO_LINGER, pbrpcServerConfiguration.getSoLinger());
        bootstrap.childOption(ChannelOption.SO_RCVBUF, pbrpcServerConfiguration.getSoRcvbuf());
        bootstrap.childOption(ChannelOption.SO_SNDBUF, pbrpcServerConfiguration.getSoSndbuf());

        ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        "idlestate",
                        new IdleStateHandler(IDLE_CHANNEL_TIMEOUT, IDLE_CHANNEL_TIMEOUT,
                                IDLE_CHANNEL_TIMEOUT));
                ch.pipeline().addLast("idle", new RpcServerChannelIdleHandler());
                ch.pipeline().addLast("deser", new PbrpcMessageDeserializer());
                ch.pipeline().addLast("coreHandler", new PbrpcServerHandler(serviceLocator));
                ch.pipeline().addLast("ser", new PbrpcMessageSerializer());
            }
        };
        bootstrap.group(bossGroup, workerGroup).childHandler(initializer);

        LOG.info("Pbrpc server init done");
    }

    /**
     * 启动服务
     */
    public void start() {
        LOG.info("Pbrpc server is about to start on port " + port);
        try {
            bootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            LOG.error("Server failed to start, " + e.getMessage(), e);
        }
        LOG.info("Server started");
    }

    /**
     * 关闭服务
     */
    public void shutdown() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 注册某个bean到服务中暴露
     * 
     * @param key
     *            服务的全局唯一标示
     * @param serviceBean
     *            服务bean对象
     */
    public void register(Integer key, Object serviceBean) {
        serviceLocator.regiserService(key, serviceBean);
    }

    /**
     * 发布服务
     * 
     * @param key
     *            服务的全局唯一标示
     * @param serviceBean
     *            服务bean对象
     */
    public void publish(Integer key, Object serviceBean) {
        serviceLocator.publishService();
    }

}
