package com.baidu.beidou.navi.pbrpc.client;

import io.netty.channel.ChannelFuture;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.client.callback.CallFuture;
import com.baidu.beidou.navi.pbrpc.exception.client.OperationNotSupportException;
import com.baidu.beidou.navi.pbrpc.exception.client.PbrpcException;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;
import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: PooledPbrpcClient <br/>
 * Function: 使用连接池技术的客户端
 * 
 * @author Zhang Xu
 */
public class PooledPbrpcClient implements PbrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(PooledPbrpcClient.class);

    /**
     * socket连接池
     */
    private PbrpcClientChannelPool channelPool;

    /**
     * 连接池配置
     */
    private GenericObjectPool.Config pooledConfig;

    /**
     * 远程服务IP
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

    public PooledPbrpcClient() {
    }

    /**
     * Creates a new instance of PooledPbrpcClient.
     * 
     * @param configuration
     *            连接池配置
     * @param clientConfig
     *            客户端配置
     * @param ip
     *            socket远程ip
     * @param port
     *            socket远程端口
     * @param connTimeout
     *            客户端连接时间，单位为毫秒
     * @param readTimeout
     *            客户端调用的超时时间，单位为毫秒，超时则会抛出{@link com.baidu.beidou.navi.pbrpc.exception.TimeoutException}
     */
    public PooledPbrpcClient(PooledConfiguration configuration,
            PbrpcClientConfiguration clientConfig, String ip, int port, int connTimeout,
            int readTimeout) {
        this.pooledConfig = configuration.getPoolConfig();
        this.ip = ip;
        this.port = port;
        this.connTimeout = connTimeout;
        this.readTimeout = readTimeout;
        channelPool = new PbrpcClientChannelPool(pooledConfig, clientConfig, ip, port, connTimeout,
                readTimeout);
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#asyncTransport(java.lang.Class,
     *      com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg)
     */
    @Override
    public <T extends GeneratedMessage> CallFuture<T> asyncTransport(Class<T> responseClazz,
            PbrpcMsg pbrpcMsg) {
        PbrpcClientChannel channel = channelPool.getResource();
        try {
            CallFuture<T> res = channel.asyncTransport(responseClazz, pbrpcMsg, this.readTimeout);
            return res;
        } catch (Exception e) {
            LOG.error("asyncTransport failed, " + e.getMessage(), e);
            channelPool.returnBrokenResource(channel);
            throw new PbrpcException("Pbrpc invocation failed on " + getInfo() + ", "
                    + e.getMessage(), e);
        } finally {
            channelPool.returnResource(channel);
        }
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#syncTransport(java.lang.Class,
     *      com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg)
     */
    @Override
    public <T extends GeneratedMessage> T syncTransport(Class<T> responseClazz, PbrpcMsg pbrpcMsg) {
        try {
            CallFuture<T> future = asyncTransport(responseClazz, pbrpcMsg);
            if (future != null) {
                return future.get();
            }
            return null;
        } catch (PbrpcException e) {
            throw e;
        } catch (InterruptedException e) {
            throw new PbrpcException("Pbrpc invocation failed on " + getInfo() + ", "
                    + e.getMessage(), e);
        }
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#connect()
     */
    @Override
    public ChannelFuture connect() {
        throw new OperationNotSupportException();
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#shutdown()
     */
    @Override
    public void shutdown() {
        channelPool.destroy();
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#getInfo()
     */
    @Override
    public String getInfo() {
        return ip + ":" + port + ", connTimeout=" + connTimeout + ", readTimeout=" + readTimeout;
    }

}
