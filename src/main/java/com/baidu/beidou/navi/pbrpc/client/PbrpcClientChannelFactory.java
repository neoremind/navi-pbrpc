package com.baidu.beidou.navi.pbrpc.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassName: ClientConnectionFactory <br/>
 * Function: 连接池对象构造工厂
 * 
 * @author Zhang Xu
 */
public class PbrpcClientChannelFactory extends BasePoolableObjectFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PbrpcClientChannelFactory.class);

    /**
     * pbrpc客户端连接
     */
    private PbrpcClient pbrpcClient;

    /**
     * Creates a new instance of ClientConnectionFactory.
     * 
     * @param clientConfig
     * @param ip
     * @param port
     * @param connTimeout
     * @param readTimeout
     */
    public PbrpcClientChannelFactory(PbrpcClientConfiguration clientConfig, String ip, int port,
            int connTimeout, int readTimeout) {
        pbrpcClient = new SimplePbrpcClient(clientConfig, false, ip, port, connTimeout, readTimeout);
    }

    /**
     * @see org.apache.commons.pool.BasePoolableObjectFactory#makeObject()
     */
    public Object makeObject() throws Exception {
        PbrpcClientChannel ch = new PbrpcClientChannel();

        ChannelFuture future = pbrpcClient.connect();

        future.awaitUninterruptibly();
        if (!future.isSuccess()) {
            LOG.warn("Making new connection on " + pbrpcClient.getInfo() + " not success",
                    future.cause());
        }

        LOG.info("Making new connection on " + pbrpcClient.getInfo() + " and adding to pool done");
        ch.setChannelFuture(future);

        return ch;
    }

    /**
     * @see org.apache.commons.pool.BasePoolableObjectFactory#destroyObject(java.lang.Object)
     */
    public void destroyObject(final Object obj) throws Exception {
        if (obj instanceof PbrpcClientChannel) {
            final PbrpcClientChannel ch = (PbrpcClientChannel) obj;
            Channel channel = ch.getChannelFuture().channel();
            if (channel.isOpen() && channel.isActive()) {
                channel.close();
            }
            LOG.info("Closing channel and destroy connection from pool done");
        }
    }

    /**
     * @see org.apache.commons.pool.BasePoolableObjectFactory#validateObject(java.lang.Object)
     */
    public boolean validateObject(Object obj) {
        if (obj instanceof PbrpcClientChannel) {
            final PbrpcClientChannel ch = (PbrpcClientChannel) obj;
            Channel channel = ch.getChannelFuture().channel();
            return channel.isOpen() && channel.isActive();
        }
        return false;
    }

    public PbrpcClient getPbrpcClient() {
        return pbrpcClient;
    }

    public void setPbrpcClient(PbrpcClient pbrpcClient) {
        this.pbrpcClient = pbrpcClient;
    }

}
