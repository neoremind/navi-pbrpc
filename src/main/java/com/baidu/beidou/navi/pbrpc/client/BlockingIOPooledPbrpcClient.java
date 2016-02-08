package com.baidu.beidou.navi.pbrpc.client;

import io.netty.channel.ChannelFuture;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.client.callback.CallFuture;
import com.baidu.beidou.navi.pbrpc.codec.Codec;
import com.baidu.beidou.navi.pbrpc.codec.impl.ProtobufCodec;
import com.baidu.beidou.navi.pbrpc.exception.client.OperationNotSupportException;
import com.baidu.beidou.navi.pbrpc.exception.client.PbrpcException;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;
import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: BlockingIOPooledPbrpcClient <br/>
 * Function: 使用连接池技术的blocking io客户端
 * 
 * @author Zhang Xu
 */
public class BlockingIOPooledPbrpcClient implements PbrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(BlockingIOPooledPbrpcClient.class);

    /**
     * socket连接池
     */
    private BlockingIOPbrpcClientSocketPool socketPool;

    /**
     * 连接池配置
     */
    private GenericObjectPool.Config pooledConfig;

    /**
     * 客户端配置
     */
    private PbrpcClientConfiguration pbrpcClientConfiguration = new PbrpcClientConfiguration();

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

    /**
     * 可配置，默认使用protobuf来做body的序列化
     */
    private Codec codec = new ProtobufCodec();

    /**
     * header+body通讯协议方式的头解析构造器
     */
    private HeaderResolver headerResolver = new NsHeaderResolver();

    public BlockingIOPooledPbrpcClient() {
    }

    /**
     * Creates a new instance of BlockingIOPooledPbrpcClient.
     * 
     * @param pooledConfig
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
    public BlockingIOPooledPbrpcClient(PooledConfiguration configuration,
            PbrpcClientConfiguration clientConfig, String ip, int port, int connTimeout,
            int readTimeout) {
        this.pooledConfig = configuration.getPoolConfig();
        this.ip = ip;
        this.port = port;
        this.connTimeout = connTimeout;
        this.readTimeout = readTimeout;
        this.pbrpcClientConfiguration = clientConfig;
        socketPool = new BlockingIOPbrpcClientSocketPool(pooledConfig, pbrpcClientConfiguration,
                ip, port, connTimeout, readTimeout, codec, headerResolver);
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#asyncTransport(java.lang.Class,
     *      com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg)
     */
    @Override
    public <T extends GeneratedMessage> CallFuture<T> asyncTransport(Class<T> responseClazz,
            PbrpcMsg pbrpcMsg) {
        throw new OperationNotSupportException();
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#syncTransport(java.lang.Class,
     *      com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg)
     */
    @Override
    public <T extends GeneratedMessage> T syncTransport(Class<T> responseClazz, PbrpcMsg pbrpcMsg) {
        BlockingIOPbrpcClient client = socketPool.getResource();
        try {
            T res = client.syncTransport(responseClazz, pbrpcMsg);
            return res;
        } catch (Exception e) {
            LOG.error("asyncTransport failed, " + e.getMessage(), e);
            socketPool.returnBrokenResource(client);
            throw new PbrpcException("Pbrpc invocation failed on " + getInfo() + ", "
                    + e.getMessage(), e);
        } finally {
            socketPool.returnResource(client);
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
        socketPool.destroy();
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#getInfo()
     */
    @Override
    public String getInfo() {
        return ip + ":" + port + ", connTimeout=" + connTimeout + ", readTimeout=" + readTimeout;
    }

    public void setPooledConfig(GenericObjectPool.Config pooledConfig) {
        this.pooledConfig = pooledConfig;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setConnTimeout(int connTimeout) {
        this.connTimeout = connTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setCodec(Codec codec) {
        this.codec = codec;
    }

    public void setHeaderResolver(HeaderResolver headerResolver) {
        this.headerResolver = headerResolver;
    }

    public void setPbrpcClientConfiguration(PbrpcClientConfiguration pbrpcClientConfiguration) {
        this.pbrpcClientConfiguration = pbrpcClientConfiguration;
    }

}
