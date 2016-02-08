package com.baidu.beidou.navi.pbrpc.client;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.codec.Codec;
import com.baidu.beidou.navi.pbrpc.util.Pool;

/**
 * ClassName: BlockingIOPbrpcClientSocketPool <br/>
 * Function: 客户端连接池，也叫做socket池，转为blocking io使用
 * 
 * @author Zhang Xu
 */
public class BlockingIOPbrpcClientSocketPool extends Pool<BlockingIOPbrpcClient> {

    private static final Logger LOG = LoggerFactory
            .getLogger(BlockingIOPbrpcClientSocketPool.class);

    /**
     * Creates a new instance of BlockingIOPbrpcClientSocketPool.
     * 
     * @param poolConfig
     * @param clientConfig
     * @param host
     * @param port
     * @param connTimeout
     * @param readTimeout
     * @param codec
     * @param headerResolver
     */
    public BlockingIOPbrpcClientSocketPool(final Config poolConfig,
            final PbrpcClientConfiguration clientConfig, final String host, int port,
            int connTimeout, int readTimeout, Codec codec, HeaderResolver headerResolver) {
        super(poolConfig, new BlockingIOPbrpcClientSocketFactory(clientConfig, host, port,
                connTimeout, readTimeout, codec, headerResolver));
        LOG.info("Init connection pool done but connections will not be established until you start using the pool");
    }

}
