package com.baidu.beidou.navi.pbrpc.client;

import java.util.ArrayList;
import java.util.List;

import com.baidu.beidou.navi.pbrpc.client.ha.ConnectStringParser;
import com.baidu.beidou.navi.pbrpc.client.ha.IpPort;
import com.baidu.beidou.navi.pbrpc.client.ha.LoadBalanceStrategy;
import com.baidu.beidou.navi.pbrpc.util.PbrpcConstants;

/**
 * ClassName: HAPbrpcClientFactory <br/>
 * Function: 构建Pbrpc调用客户端的工厂，可以负责构造高可用的客户端连接，短连接或者长连接均支持
 * 
 * @author Zhang Xu
 */
public class HAPbrpcClientFactory {

    /**
     * 获取短连接调用客户端
     * 
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildShortLiveConnection(String connectString,
            LoadBalanceStrategy lb) {
        return buildShortLiveConnection(new PbrpcClientConfiguration(), connectString,
                PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT,
                PbrpcConstants.DEFAULT_CLIENT_READ_TIMEOUT, lb);
    }

    /**
     * 获取短连接调用客户端
     * 
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildShortLiveConnection(String connectString, int readTimeout,
            LoadBalanceStrategy lb) {
        return buildShortLiveConnection(new PbrpcClientConfiguration(), connectString,
                PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT, readTimeout, lb);
    }

    /**
     * 获取短连接调用客户端
     * 
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param connTimeout
     *            客户端连接时间，单位毫秒
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildShortLiveConnection(String connectString, int connTimeout,
            int readTimeout, LoadBalanceStrategy lb) {
        return buildShortLiveConnection(new PbrpcClientConfiguration(), connectString, connTimeout,
                readTimeout, lb);
    }

    /**
     * 获取短连接调用客户端
     * 
     * @param configuration
     *            客户端配置
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param connTimeout
     *            客户端连接时间，单位毫秒
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildShortLiveConnection(PbrpcClientConfiguration configuration,
            String connectString, int connTimeout, int readTimeout, LoadBalanceStrategy lb) {
        List<IpPort> ipPortList = ConnectStringParser.resolveConnectString(connectString);
        List<PbrpcClient> clientList = new ArrayList<PbrpcClient>();
        for (IpPort ipPort : ipPortList) {
            clientList.add(new SimplePbrpcClient(configuration, ipPort.getIp(), ipPort.getPort(),
                    connTimeout, readTimeout));
        }
        return new HAPbrpcClient(clientList).setLoadBalanceStrategy(lb);
    }

    /**
     * 获取长连接池化的客户端
     * 
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildPooledConnection(String connectString, LoadBalanceStrategy lb) {
        return buildPooledConnection(new PooledConfiguration(), new PbrpcClientConfiguration(),
                connectString, PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT,
                PbrpcConstants.DEFAULT_CLIENT_READ_TIMEOUT, lb);
    }

    /**
     * 获取长连接池化的客户端
     * 
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildPooledConnection(String connectString, int readTimeout,
            LoadBalanceStrategy lb) {
        return buildPooledConnection(new PooledConfiguration(), new PbrpcClientConfiguration(),
                connectString, PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT, readTimeout, lb);
    }

    /**
     * 获取长连接池化的客户端
     * 
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param connTimeout
     *            客户端连接时间，单位毫秒
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildPooledConnection(String connectString, int connTimeout,
            int readTimeout, LoadBalanceStrategy lb) {
        return buildPooledConnection(new PooledConfiguration(), new PbrpcClientConfiguration(),
                connectString, connTimeout, readTimeout, lb);
    }

    /**
     * 获取长连接池化的客户端
     * 
     * @param configuration
     *            连接池参数配置，使用<tt>commons-pool</tt>来做连接池
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildPooledConnection(PooledConfiguration configuration,
            String connectString, int readTimeout, LoadBalanceStrategy lb) {
        return buildPooledConnection(configuration, new PbrpcClientConfiguration(), connectString,
                PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT, readTimeout, lb);
    }

    /**
     * 获取长连接池化的客户端
     * 
     * @param configuration
     *            连接池参数配置，使用<tt>commons-pool</tt>来做连接池
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param connTimeout
     *            客户端连接时间，单位毫秒
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildPooledConnection(PooledConfiguration configuration,
            String connectString, int connTimeout, int readTimeout, LoadBalanceStrategy lb) {
        return buildPooledConnection(configuration, new PbrpcClientConfiguration(), connectString,
                connTimeout, readTimeout, lb);
    }

    /**
     * 获取长连接池化的客户端
     * 
     * @param configuration
     *            连接池参数配置，使用<tt>commons-pool</tt>来做连接池
     * @param clientConfig
     *            客户端配置
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param connTimeout
     *            客户端连接时间，单位毫秒
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildPooledConnection(PooledConfiguration configuration,
            PbrpcClientConfiguration clientConfig, String connectString, int connTimeout,
            int readTimeout, LoadBalanceStrategy lb) {
        List<IpPort> ipPortList = ConnectStringParser.resolveConnectString(connectString);
        List<PbrpcClient> clientList = new ArrayList<PbrpcClient>();
        for (IpPort ipPort : ipPortList) {
            clientList.add(new PooledPbrpcClient(configuration, clientConfig, ipPort.getIp(),
                    ipPort.getPort(), connTimeout, readTimeout));
        }
        return new HAPbrpcClient(clientList).setLoadBalanceStrategy(lb);
    }

    /**
     * 获取短连接调用blocking io客户端
     * 
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildShortLiveBlockingIOConnection(String connectString,
            LoadBalanceStrategy lb) {
        return buildShortLiveBlockingIOConnection(new PbrpcClientConfiguration(), connectString,
                PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT,
                PbrpcConstants.DEFAULT_CLIENT_READ_TIMEOUT, lb);
    }

    /**
     * 获取短连接调用blocking io客户端
     * 
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildShortLiveBlockingIOConnection(String connectString,
            int readTimeout, LoadBalanceStrategy lb) {
        return buildShortLiveBlockingIOConnection(new PbrpcClientConfiguration(), connectString,
                PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT, readTimeout, lb);
    }

    /**
     * 获取短连接调用blocking io客户端
     * 
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param connTimeout
     *            客户端连接时间，单位毫秒
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildShortLiveBlockingIOConnection(String connectString,
            int connTimeout, int readTimeout, LoadBalanceStrategy lb) {
        return buildShortLiveBlockingIOConnection(new PbrpcClientConfiguration(), connectString,
                connTimeout, readTimeout, lb);
    }

    /**
     * 获取短连接调用blocking io客户端
     * 
     * @param configuration
     *            客户端配置
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param connTimeout
     *            客户端连接时间，单位毫秒
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildShortLiveBlockingIOConnection(
            PbrpcClientConfiguration configuration, String connectString, int connTimeout,
            int readTimeout, LoadBalanceStrategy lb) {
        List<IpPort> ipPortList = ConnectStringParser.resolveConnectString(connectString);
        List<PbrpcClient> clientList = new ArrayList<PbrpcClient>();
        for (IpPort ipPort : ipPortList) {
            clientList.add(new BlockingIOPbrpcClient(configuration, true, ipPort.getIp(), ipPort
                    .getPort(), connTimeout, readTimeout));
        }
        return new HAPbrpcClient(clientList).setLoadBalanceStrategy(lb);
    }

    /**
     * 获取长连接池化的blocking io客户端
     * 
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildPooledBlockingIOConnection(String connectString,
            LoadBalanceStrategy lb) {
        return buildPooledBlockingIOConnection(new PooledConfiguration(),
                new PbrpcClientConfiguration(), connectString,
                PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT,
                PbrpcConstants.DEFAULT_CLIENT_READ_TIMEOUT, lb);
    }

    /**
     * 获取长连接池化的blocking io客户端
     * 
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildPooledBlockingIOConnection(String connectString,
            int readTimeout, LoadBalanceStrategy lb) {
        return buildPooledBlockingIOConnection(new PooledConfiguration(),
                new PbrpcClientConfiguration(), connectString,
                PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT, readTimeout, lb);
    }

    /**
     * 获取长连接池化的blocking io客户端
     * 
     * @param configuration
     *            连接池参数配置，使用<tt>commons-pool</tt>来做连接池
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildPooledBlockingIOConnection(PooledConfiguration configuration,
            String connectString, int readTimeout, LoadBalanceStrategy lb) {
        return buildPooledBlockingIOConnection(configuration, new PbrpcClientConfiguration(),
                connectString, PbrpcConstants.DEFAULT_CLIENT_CONN_TIMEOUT, readTimeout, lb);
    }

    /**
     * 获取长连接池化的blocking io客户端
     * 
     * @param configuration
     *            连接池参数配置，使用<tt>commons-pool</tt>来做连接池
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param connTimeout
     *            客户端连接时间，单位毫秒
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildPooledBlockingIOConnection(String connectString,
            int connTimeout, int readTimeout, LoadBalanceStrategy lb) {
        return buildPooledBlockingIOConnection(new PooledConfiguration(),
                new PbrpcClientConfiguration(), connectString, connTimeout, readTimeout, lb);
    }

    /**
     * 获取长连接池化的blocking io客户端
     * 
     * @param configuration
     *            连接池参数配置，使用<tt>commons-pool</tt>来做连接池
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param connTimeout
     *            客户端连接时间，单位毫秒
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildPooledBlockingIOConnection(PooledConfiguration configuration,
            String connectString, int connTimeout, int readTimeout, LoadBalanceStrategy lb) {
        return buildPooledBlockingIOConnection(configuration, new PbrpcClientConfiguration(),
                connectString, connTimeout, readTimeout, lb);
    }

    /**
     * 获取长连接池化的blocking io客户端
     * 
     * @param configuration
     *            连接池参数配置，使用<tt>commons-pool</tt>来做连接池
     * @param clientConfig
     *            客户端配置
     * @param connectString
     *            连接初始化串，例如"127.0.0.1:8080,2.2.2.2:9999"
     * @param connTimeout
     *            客户端连接时间，单位毫秒
     * @param readTimeout
     *            客户端调用时间，单位毫秒
     * @param lb
     *            负载均衡策略
     * @return HAPbrpcClient
     */
    public static HAPbrpcClient buildPooledBlockingIOConnection(PooledConfiguration configuration,
            PbrpcClientConfiguration clientConfig, String connectString, int connTimeout,
            int readTimeout, LoadBalanceStrategy lb) {
        List<IpPort> ipPortList = ConnectStringParser.resolveConnectString(connectString);
        List<PbrpcClient> clientList = new ArrayList<PbrpcClient>();
        for (IpPort ipPort : ipPortList) {
            clientList.add(new BlockingIOPooledPbrpcClient(configuration, clientConfig, ipPort
                    .getIp(), ipPort.getPort(), connTimeout, readTimeout));
        }
        return new HAPbrpcClient(clientList).setLoadBalanceStrategy(lb);
    }

}
