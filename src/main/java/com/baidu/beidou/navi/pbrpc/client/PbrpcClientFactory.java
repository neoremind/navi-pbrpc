package com.baidu.beidou.navi.pbrpc.client;

import com.baidu.beidou.navi.pbrpc.util.PbrpcConstants;

/**
 * ClassName: PbrpcClientFactory <br/>
 * Function: 构建Pbrpc调用客户端的工厂，可以负责构建短连接或者长连接池化的客户端调用stub
 * 
 * @author Zhang Xu
 */
public class PbrpcClientFactory {

    /**
     * 获取短连接调用客户端
     * 
     * @param ip
     *            远程服务ip
     * @param port
     *            远程服务端口
     * @return SimplePbrpcClient
     */
    public static SimplePbrpcClient buildShortLiveConnection(String ip, int port) {
        return buildShortLiveConnection(ip, port, PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT,
                PbrpcConstants.DEFAULT_CLIENT_READ_TIMEOUT);
    }

    /**
     * 获取短连接调用客户端
     * 
     * @param ip
     *            远程服务ip
     * @param port
     *            远程服务端口
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @return SimplePbrpcClient
     */
    public static SimplePbrpcClient buildShortLiveConnection(String ip, int port, int readTimeout) {
        return buildShortLiveConnection(ip, port, PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT,
                readTimeout);
    }

    /**
     * 获取短连接调用客户端
     * 
     * @param ip
     *            远程服务ip
     * @param port
     *            远程服务端口
     * @param connTimeout
     *            客户端连接时间，单位毫秒
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @return SimplePbrpcClient
     */
    public static SimplePbrpcClient buildShortLiveConnection(String ip, int port, int connTimeout,
            int readTimeout) {
        SimplePbrpcClient client = new SimplePbrpcClient(ip, port, connTimeout, readTimeout);
        return client;
    }

    /**
     * 获取长连接池化的客户端
     * 
     * @param ip
     *            远程服务ip
     * @param port
     *            远程服务端口
     * @return PooledPbrpcClient
     */
    public static PooledPbrpcClient buildPooledConnection(String ip, int port) {
        return buildPooledConnection(new PooledConfiguration(), ip, port,
                PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT,
                PbrpcConstants.DEFAULT_CLIENT_READ_TIMEOUT);
    }

    /**
     * 获取长连接池化的客户端
     * 
     * @param ip
     *            远程服务ip
     * @param port
     *            远程服务端口
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @return PooledPbrpcClient
     */
    public static PooledPbrpcClient buildPooledConnection(String ip, int port, int readTimeout) {
        return buildPooledConnection(new PooledConfiguration(), ip, port,
                PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT, readTimeout);
    }

    /**
     * 获取长连接池化的客户端
     * 
     * @param configuration
     *            连接池参数配置，使用<tt>commons-pool</tt>来做连接池
     * @param ip
     *            远程服务ip
     * @param port
     *            远程服务端口
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @return PooledPbrpcClient
     */
    public static PooledPbrpcClient buildPooledConnection(PooledConfiguration configuration,
            String ip, int port, int readTimeout) {
        return buildPooledConnection(configuration, ip, port,
                PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT, readTimeout);
    }

    /**
     * 获取长连接池化的客户端
     * 
     * @param configuration
     *            连接池参数配置，使用<tt>commons-pool</tt>来做连接池
     * @param ip
     *            远程服务ip
     * @param port
     *            远程服务端口
     * @param connTimeout
     *            客户端连接时间，单位毫秒
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @return PooledPbrpcClient
     */
    public static PooledPbrpcClient buildPooledConnection(PooledConfiguration configuration,
            String ip, int port, int connTimeout, int readTimeout) {
        PooledPbrpcClient client = new PooledPbrpcClient(configuration,
                new PbrpcClientConfiguration(), ip, port, connTimeout, readTimeout);
        return client;
    }

    /**
     * 获取短连接BIO调用客户端
     * 
     * @param ip
     *            远程服务ip
     * @param port
     *            远程服务端口
     * @return BlockingIOPbrpcClient
     */
    public static BlockingIOPbrpcClient buildShortLiveBlockingIOConnection(String ip, int port) {
        return buildShortLiveBlockingIOConnection(ip, port,
                PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT,
                PbrpcConstants.DEFAULT_CLIENT_READ_TIMEOUT);
    }

    /**
     * 获取短连接BIO调用客户端
     * 
     * @param ip
     *            远程服务ip
     * @param port
     *            远程服务端口
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @return BlockingIOPbrpcClient
     */
    public static BlockingIOPbrpcClient buildShortLiveBlockingIOConnection(String ip, int port,
            int readTimeout) {
        return buildShortLiveBlockingIOConnection(ip, port,
                PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT, readTimeout);
    }

    /**
     * 获取短连接BIO调用客户端
     * 
     * @param ip
     *            远程服务ip
     * @param port
     *            远程服务端口
     * @param connTimeout
     *            客户端连接时间，单位毫秒
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @return BlockingIOPbrpcClient
     */
    public static BlockingIOPbrpcClient buildShortLiveBlockingIOConnection(String ip, int port,
            int connTimeout, int readTimeout) {
        BlockingIOPbrpcClient client = new BlockingIOPbrpcClient(ip, port, connTimeout, readTimeout);
        return client;
    }

    /**
     * 获取长连接池化的blocking io客户端
     * 
     * @param ip
     *            远程服务ip
     * @param port
     *            远程服务端口
     * @return BlockingIOPooledPbrpcClient
     */
    public static BlockingIOPooledPbrpcClient buildPooledBlockingIOConnection(String ip, int port) {
        return buildPooledBlockingIOConnection(new PooledConfiguration(), ip, port,
                PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT,
                PbrpcConstants.DEFAULT_CLIENT_READ_TIMEOUT);
    }

    /**
     * 获取长连接池化的blocking io客户端
     * 
     * @param ip
     *            远程服务ip
     * @param port
     *            远程服务端口
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @return BlockingIOPooledPbrpcClient
     */
    public static BlockingIOPooledPbrpcClient buildPooledBlockingIOConnection(String ip, int port,
            int readTimeout) {
        return buildPooledBlockingIOConnection(new PooledConfiguration(), ip, port,
                PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT, readTimeout);
    }

    /**
     * 获取长连接池化的blocking io客户端
     * 
     * @param configuration
     *            连接池参数配置，使用<tt>commons-pool</tt>来做连接池
     * @param ip
     *            远程服务ip
     * @param port
     *            远程服务端口
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @return BlockingIOPooledPbrpcClient
     */
    public static BlockingIOPooledPbrpcClient buildPooledBlockingIOConnection(
            PooledConfiguration configuration, String ip, int port, int readTimeout) {
        return buildPooledBlockingIOConnection(configuration, ip, port,
                PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT, readTimeout);
    }

    /**
     * 获取长连接池化的blocking io客户端
     * 
     * @param configuration
     *            连接池参数配置，使用<tt>commons-pool</tt>来做连接池
     * @param ip
     *            远程服务ip
     * @param port
     *            远程服务端口
     * @param connTimeout
     *            客户端连接时间，单位毫秒
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @return BlockingIOPooledPbrpcClient
     */
    public static BlockingIOPooledPbrpcClient buildPooledBlockingIOConnection(
            PooledConfiguration configuration, String ip, int port, int connTimeout, int readTimeout) {
        BlockingIOPooledPbrpcClient client = new BlockingIOPooledPbrpcClient(configuration,
                new PbrpcClientConfiguration(), ip, port, connTimeout, readTimeout);
        return client;
    }

}
