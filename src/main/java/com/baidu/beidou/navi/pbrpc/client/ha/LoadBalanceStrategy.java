package com.baidu.beidou.navi.pbrpc.client.ha;

import java.util.List;

import com.baidu.beidou.navi.pbrpc.client.PbrpcClient;
import com.baidu.beidou.navi.pbrpc.client.callback.CallFuture;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;
import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: LoadBalanceStrategy <br/>
 * Function: 负载均衡器
 * 
 * @author Zhang Xu
 */
public interface LoadBalanceStrategy {

    /**
     * 根据客户端的连接采用负载均衡策略调用，异步调用
     * 
     * @param clientList
     *            客户端列表
     * @param responseClazz
     *            返回对象的类型
     * @param pbrpcMsg
     *            pbrpc消息
     * @return 调用future
     */
    <T extends GeneratedMessage> CallFuture<T> doAsyncTransport(List<PbrpcClient> clientList,
            Class<T> responseClazz, PbrpcMsg pbrpcMsg);

    /**
     * 根据客户端的连接采用负载均衡策略调用，同步调用
     * 
     * @param clientList
     *            客户端列表
     * @param responseClazz
     *            返回对象的类型
     * @param pbrpcMsg
     *            pbrpc消息
     * @return 调用结果
     */
    <T extends GeneratedMessage> T doSyncTransport(List<PbrpcClient> clientList,
            Class<T> responseClazz, PbrpcMsg pbrpcMsg);

}
