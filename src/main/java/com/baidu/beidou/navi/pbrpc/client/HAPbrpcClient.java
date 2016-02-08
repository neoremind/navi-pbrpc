package com.baidu.beidou.navi.pbrpc.client;

import io.netty.channel.ChannelFuture;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.client.callback.CallFuture;
import com.baidu.beidou.navi.pbrpc.client.ha.LoadBalanceStrategy;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;
import com.baidu.beidou.navi.pbrpc.util.Preconditions;
import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: HAPbrpcClient <br/>
 * Function: 高可用的pbrpc客户端
 *
 * @author Zhang Xu
 */
public class HAPbrpcClient implements PbrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(HAPbrpcClient.class);

    /**
     * 负载均衡策略
     */
    private LoadBalanceStrategy loadBalanceStrategy;

    /**
     * 客户端list
     */
    private List<PbrpcClient> clientList;

    public HAPbrpcClient() {
    }

    /**
     * Creates a new instance of HAPbrpcClient.
     *
     * @param clientList
     */
    public HAPbrpcClient(List<PbrpcClient> clientList) {
        this.clientList = clientList;
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#connect()
     */
    @Override
    public ChannelFuture connect() {
        throw new IllegalStateException(this.getClass().getSimpleName()
                + " does not need to call connect");
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#shutdown()
     */
    @Override
    public void shutdown() {
        if (clientList != null) {
            for (PbrpcClient client : clientList) {
                LOG.info("Start to destroy " + client.getInfo());
                client.shutdown();
            }
        }
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#asyncTransport(java.lang.Class,
     * com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg)
     */
    @Override
    public <T extends GeneratedMessage> CallFuture<T> asyncTransport(Class<T> responseClazz,
                                                                     PbrpcMsg pbrpcMsg) {
        Preconditions.checkNotNull(loadBalanceStrategy, "Load balance strategy is not init");
        return loadBalanceStrategy.doAsyncTransport(clientList, responseClazz, pbrpcMsg);
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#syncTransport(java.lang.Class,
     * com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg)
     */
    @Override
    public <T extends GeneratedMessage> T syncTransport(Class<T> responseClazz, PbrpcMsg pbrpcMsg) {
        Preconditions.checkNotNull(loadBalanceStrategy, "Load balance strategy is not init");
        return loadBalanceStrategy.doSyncTransport(clientList, responseClazz, pbrpcMsg);
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#getInfo()
     */
    @Override
    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("HA pbrpc client ");
        if (clientList != null) {
            for (PbrpcClient client : clientList) {
                sb.append("[");
                sb.append(client.getInfo());
                sb.append("]");
            }
        }
        return sb.toString();
    }

    /**
     * 设置负载均衡器
     *
     * @param loadBalanceStrategy
     *
     * @return
     */
    public HAPbrpcClient setLoadBalanceStrategy(LoadBalanceStrategy loadBalanceStrategy) {
        this.loadBalanceStrategy = loadBalanceStrategy;
        return this;
    }

    public void setClientList(List<PbrpcClient> clientList) {
        this.clientList = clientList;
    }
}
