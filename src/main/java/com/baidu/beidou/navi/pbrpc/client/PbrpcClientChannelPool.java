package com.baidu.beidou.navi.pbrpc.client;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.util.Pool;

/**
 * ClassName: PbrpcClientChannelPool <br/>
 * Function: 客户端连接池，也叫做信道池
 * 
 * @author Zhang Xu
 */
public class PbrpcClientChannelPool extends Pool<PbrpcClientChannel> {

    private static final Logger LOG = LoggerFactory.getLogger(PbrpcClientChannelPool.class);

    /**
     * Creates a new instance of PbrpcClientConnectionPool.
     * 
     * @param poolConfig
     * @param clientConfig
     * @param host
     * @param port
     * @param connTimeout
     * @param readTimeout
     */
    public PbrpcClientChannelPool(final Config poolConfig,
            final PbrpcClientConfiguration clientConfig, final String host, int port,
            int connTimeout, int readTimeout) {
        super(poolConfig, new PbrpcClientChannelFactory(clientConfig, host, port, connTimeout,
                readTimeout));
        LOG.info("Init connection pool done but connections will not be established until you start using the pool");
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.util.Pool#returnBrokenResource(java.lang.Object)
     */
    public void returnBrokenResource(final PbrpcClientChannel resource) {
        returnBrokenResourceObject(resource);
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.util.Pool#returnResource(java.lang.Object)
     */
    public void returnResource(final PbrpcClientChannel resource) {
        returnResourceObject(resource);
    }

}
