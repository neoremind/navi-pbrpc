package com.baidu.beidou.navi.pbrpc.client;

import com.baidu.beidou.navi.pbrpc.client.ha.LoadBalanceStrategy;

/**
 * 基于IP:PORT地址串的服务定位器
 *
 * @author zhangxu
 */
public class IpPortShortLiveBlockingIOPbrpcServerLocator implements HAPbrpcServerLocator {

    /**
     * 标示（例如，IP:PORT串、或者LDAP名称）来获取远程服务实际IP:PORT列表，进而构造一个高可用的Pbrpc调用客户端
     *
     * @param serverSign  标示（例如，IP:PORT串、或者LDAP名称）
     * @param connTimeout 连接超时
     * @param readTimeout 读超时
     * @param lb          负载均衡策略
     *
     * @return 高可用Pbrpc客户端
     */
    @Override
    public HAPbrpcClient factory(String serverSign, int connTimeout, int readTimeout,
                                 LoadBalanceStrategy lb) {
        return HAPbrpcClientFactory.buildShortLiveBlockingIOConnection(new PbrpcClientConfiguration(), serverSign,
                connTimeout, readTimeout, lb);
    }

}
