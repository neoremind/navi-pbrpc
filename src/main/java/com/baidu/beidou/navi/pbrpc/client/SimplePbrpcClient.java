package com.baidu.beidou.navi.pbrpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.client.callback.CallFuture;
import com.baidu.beidou.navi.pbrpc.client.callback.CallbackPool;
import com.baidu.beidou.navi.pbrpc.client.handler.PbrpcClientHandler;
import com.baidu.beidou.navi.pbrpc.exception.client.PbrpcConnectionException;
import com.baidu.beidou.navi.pbrpc.exception.client.PbrpcException;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMessageDeserializer;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMessageSerializer;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;
import com.baidu.beidou.navi.pbrpc.util.IdGenerator;
import com.baidu.beidou.navi.pbrpc.util.PbrpcConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: SimplePbrpcClient <br/>
 * Function: 简单的远程访问客户端，短连接会直接使用，由channelHandler负责关闭连接；对于长连接不关闭channel，复用之
 * 
 * @author Zhang Xu
 */
public class SimplePbrpcClient implements PbrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(SimplePbrpcClient.class);

    /**
     * 远程服务ip地址
     */
    private String ip;

    /**
     * 远程服务端口
     */
    private int port;

    /**
     * 客户端连接超时，单位毫秒
     */
    private int connTimeout;

    /**
     * 客户端调用超时，单位毫秒
     */
    private int readTimeout;

    /**
     * 客户端配置
     */
    private PbrpcClientConfiguration pbrpcClientConfiguration = new PbrpcClientConfiguration();

    /**
     * netty的<code>Bootstrap</code>
     */
    private Bootstrap bootstrap;

    /**
     * netty的<code>NioEventLoopGroup</code>
     */
    private NioEventLoopGroup eventLoopGroup;

    /**
     * 每次连接的<code>Channel</code>，对于长连接会保持在实例中，对于短连接每次新建一个
     */
    private Channel channel;

    /**
     * 是否为短连接调用
     */
    private boolean isShortAliveConn;

    /**
     * 检测客户端是否超时的检测器已经启动，如果启动了则不会重复启动
     */
    private static volatile AtomicBoolean isimeoutEvictorStarted = new AtomicBoolean(false);

    /**
     * 启动检测客户端调用超时的检测器
     */
    public void startTimeoutEvictor() {
        // 保证所有client线程共用一个timeout检测器
        if (isimeoutEvictorStarted.compareAndSet(false, true)) {
            LOG.info("Start timeout evictor, delayStartTime="
                    + PbrpcConstants.CLIENT_TIMEOUT_EVICTOR_DELAY_START_TIME + ", checkInterval="
                    + PbrpcConstants.CLIENT_TIMEOUT_EVICTOR_CHECK_INTERVAL);
            TimeoutEvictor evictor = new TimeoutEvictor();
            TimeoutEvictionTimer.schedule(evictor,
                    PbrpcConstants.CLIENT_TIMEOUT_EVICTOR_DELAY_START_TIME,
                    PbrpcConstants.CLIENT_TIMEOUT_EVICTOR_CHECK_INTERVAL);
        }
    }

    public SimplePbrpcClient() {
    }

    /**
     * Creates a new instance of ShortLiveConnectionPbrpcClient.
     * <p>
     * 默认为短连接调用
     * 
     * @param ip
     * @param port
     * @param connTimeout
     * @param readTimeout
     */
    public SimplePbrpcClient(String ip, int port, int connTimeout, int readTimeout) {
        this(null, true, ip, port, connTimeout, readTimeout);
    }

    /**
     * Creates a new instance of ShortLiveConnectionPbrpcClient.
     * <p>
     * 默认为短连接调用
     * 
     * @param pbrpcClientConfiguration
     * @param ip
     * @param port
     * @param connTimeout
     * @param readTimeout
     */
    public SimplePbrpcClient(PbrpcClientConfiguration pbrpcClientConfiguration, String ip,
            int port, int connTimeout, int readTimeout) {
        this(pbrpcClientConfiguration, true, ip, port, connTimeout, readTimeout);
    }

    /**
     * Creates a new instance of ShortLiveConnectionPbrpcClient.
     * <p>
     * 不能暴露该构造方法给客户端使用，原因是如果<tt>isShortAliveConn</tt>设置为true，则默认不会启用IdelChannel的检测器，channel不会超时销毁。
     * 
     * @param isShortAliveConn
     * @param ip
     * @param port
     * @param connTimeout
     * @param readTimeout
     */
    SimplePbrpcClient(boolean isShortAliveConn, String ip, int port, int connTimeout,
            int readTimeout) {
        this(null, isShortAliveConn, ip, port, connTimeout, readTimeout);
    }

    /**
     * Creates a new instance of ShortLiveConnectionPbrpcClient.
     * 
     * @param pbrpcClientConfiguration
     * @param isShortLiveConn
     *            是否是短连接调用，如果是则表示每次都重新新建channel
     * @param ip
     * @param port
     * @param connTimeout
     * @param readTimeout
     */
    SimplePbrpcClient(PbrpcClientConfiguration pbrpcClientConfiguration, boolean isShortLiveConn,
            String ip, int port, int connTimeout, int readTimeout) {
        if (pbrpcClientConfiguration != null) {
            this.pbrpcClientConfiguration = pbrpcClientConfiguration;
        }
        this.ip = ip;
        this.port = port;
        this.connTimeout = connTimeout;
        this.readTimeout = readTimeout;
        this.isShortAliveConn = isShortLiveConn;
        bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.connTimeout);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, this.pbrpcClientConfiguration.isSoKeepalive());
        bootstrap.option(ChannelOption.SO_REUSEADDR, this.pbrpcClientConfiguration.isSoReuseaddr());
        bootstrap.option(ChannelOption.TCP_NODELAY, this.pbrpcClientConfiguration.isTcpNodelay());
        bootstrap.option(ChannelOption.SO_RCVBUF, this.pbrpcClientConfiguration.getSoRcvbuf());
        bootstrap.option(ChannelOption.SO_SNDBUF, this.pbrpcClientConfiguration.getSoSndbuf());

        ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new PbrpcMessageSerializer());
                ch.pipeline().addLast(new PbrpcMessageDeserializer());
                ch.pipeline().addLast(new PbrpcClientHandler());
            }
        };
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup).handler(initializer);

        startTimeoutEvictor();
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#connect()
     */
    public ChannelFuture connect() {
        try {
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(ip, port));
            channel = future.channel();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        LOG.info("Connection " + channelFuture.channel() + " is well established");
                    } else {
                        LOG.warn(String.format("Connection get failed on %s due to %s",
                                channelFuture.cause().getMessage(), channelFuture.cause()));
                    }
                }
            });
            return future;
        } catch (Exception e) {
            LOG.error("Failed to connect to " + getInfo() + " due to " + e.getMessage(), e);
            throw new PbrpcConnectionException(e);
        }
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#asyncTransport(java.lang.Class,
     *      com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg)
     */
    public <T extends GeneratedMessage> CallFuture<T> asyncTransport(Class<T> responseClazz,
            PbrpcMsg pbrpcMsg) {
        try {
            if (isShortAliveConn) {
                ChannelFuture channelFuture = connect().sync();
                Channel ch = channelFuture.channel();
                return doAsyncTransport(ch, responseClazz, pbrpcMsg);
            } else {
                return doAsyncTransport(this.channel, responseClazz, pbrpcMsg);
            }
        } catch (Exception e) {
            LOG.error("Failed to transport to " + getInfo() + " due to " + e.getMessage(), e);
            throw new PbrpcException(e);
        }
    }

    /**
     * 使用channel进行数据发送
     * 
     * @param ch
     * @param responseClazz
     * @param pbrpcMsg
     * @return
     */
    protected <T extends GeneratedMessage> CallFuture<T> doAsyncTransport(Channel ch,
            Class<T> responseClazz, PbrpcMsg pbrpcMsg) {
        if (ch != null) {
            int uuid = IdGenerator.genUUID();
            pbrpcMsg.setLogId(uuid);
            CallFuture<T> future = CallFuture.newInstance();
            CallbackPool.put(uuid, this.readTimeout, this.isShortAliveConn, ch, responseClazz,
                    future);
            ch.writeAndFlush(pbrpcMsg);
            LOG.debug("Send message " + pbrpcMsg + " done");
            return future;
        } else {
            LOG.error("Socket channel is not well established, so failed to transport on "
                    + getInfo());
            throw new PbrpcConnectionException(
                    "Socket channel is not well established,so failed to transport on " + getInfo());
        }
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#syncTransport(java.lang.Class,
     *      com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg)
     */
    public <T extends GeneratedMessage> T syncTransport(Class<T> responseClazz, PbrpcMsg pbrpcMsg) {
        try {
            return asyncTransport(responseClazz, pbrpcMsg).get();
        } catch (InterruptedException e) {
            throw new PbrpcConnectionException("Transport failed on " + getInfo() + ", "
                    + e.getMessage(), e);
        }
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#shutdown()
     */
    public void shutdown() {
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#getInfo()
     */
    @Override
    public String getInfo() {
        return ip + ":" + port + ", connTimeout=" + this.connTimeout + ", readTimeout="
                + this.readTimeout;
    }

}
