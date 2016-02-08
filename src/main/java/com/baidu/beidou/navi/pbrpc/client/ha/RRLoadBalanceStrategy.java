package com.baidu.beidou.navi.pbrpc.client.ha;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.client.PbrpcClient;
import com.baidu.beidou.navi.pbrpc.client.callback.CallFuture;
import com.baidu.beidou.navi.pbrpc.exception.client.HAPbrpcException;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;
import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: RRLoadBalanceStrategy <br/>
 * Function: Round Robin轮训负载均衡策略调用
 *
 * @author Zhang Xu
 */
public class RRLoadBalanceStrategy implements LoadBalanceStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(RRLoadBalanceStrategy.class);

    /**
     * 线程安全计数器
     */
    private static AtomicInteger counter = new AtomicInteger();

    /**
     * 失败策略
     */
    private FailStrategy failStrategy;

    /**
     * 传输调用回调
     */
    private TransportCallback transportCallback = new DefaultTransportCallback();

    public RRLoadBalanceStrategy() {
    }

    /**
     * Creates a new instance of RRLoadBalanceStrategy.
     *
     * @param failStrategy
     */
    public RRLoadBalanceStrategy(FailStrategy failStrategy) {
        this.failStrategy = failStrategy;
    }

    /**
     * 内部抽象方法调用
     *
     * @param clientList
     * @param responseClazz
     * @param pbrpcMsg
     * @param isAsync
     *
     * @return Object
     *
     * @throws HAPbrpcException
     */
    private <T extends GeneratedMessage> Object transport(List<PbrpcClient> clientList,
                                                          Class<T> responseClazz, PbrpcMsg pbrpcMsg, boolean isAsync)
            throws HAPbrpcException {
        int start = counter.incrementAndGet() % clientList.size();
        int clientSize = clientList.size();
        for (int currRetry = 0; currRetry < failStrategy.getMaxRetryTimes()
                && currRetry < clientSize; ) {
            PbrpcClient client = clientList.get(start);
            start++;
            start %= clientSize;
            try {
                LOG.info("Call on " + client.getInfo() + " starts...");
                if (isAsync) {
                    CallFuture<T> ret = (CallFuture<T>) client.asyncTransport(responseClazz, pbrpcMsg);
                    transportCallback.onSuccess(client, clientList);
                    return ret;
                } else {
                    T ret = (T) client.syncTransport(responseClazz, pbrpcMsg);
                    transportCallback.onSuccess(client, clientList);
                    return ret;
                }
            } catch (Exception e) {
                transportCallback.onFail(client, clientList, e);
                LOG.error("Call on " + client.getInfo() + " failed due to " + e.getMessage(), e);
                if (failStrategy.isQuitImmediately(currRetry, clientSize)) {
                    throw new HAPbrpcException(e);
                }
                LOG.info("Fail over to next if available...");
                continue;
            } finally {
                currRetry++;
            }
        }
        throw new HAPbrpcException("Failed to transport on all clients");
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.ha.LoadBalanceStrategy#doAsyncTransport(java.util.List, java.lang.Class,
     * com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends GeneratedMessage> CallFuture<T> doAsyncTransport(
            List<PbrpcClient> clientList, Class<T> responseClazz, PbrpcMsg pbrpcMsg)
            throws HAPbrpcException {
        return (CallFuture<T>) transport(clientList, responseClazz, pbrpcMsg, true);
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.ha.LoadBalanceStrategy#doSyncTransport(java.util.List, java.lang.Class,
     * com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends GeneratedMessage> T doSyncTransport(List<PbrpcClient> clientList,
                                                          Class<T> responseClazz, PbrpcMsg pbrpcMsg) {
        return (T) transport(clientList, responseClazz, pbrpcMsg, false);
    }

    public void setFailStrategy(FailStrategy failStrategy) {
        this.failStrategy = failStrategy;
    }

    public void setTransportCallback(TransportCallback transportCallback) {
        this.transportCallback = transportCallback;
    }
}
