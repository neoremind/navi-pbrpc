package com.baidu.beidou.navi.pbrpc.client;

import io.netty.channel.ChannelFuture;

import com.baidu.beidou.navi.pbrpc.client.callback.CallFuture;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;
import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: PbrpcClient <br/>
 * Function: Pbrpc客户端通用调用接口
 * 
 * @author Zhang Xu
 */
public interface PbrpcClient {

    /**
     * 连接远程服务器
     * 
     * @return ChannelFuture netty nio方式连接后的future回调
     */
    ChannelFuture connect();

    /**
     * 关闭客户端的远程连接
     */
    void shutdown();

    /**
     * 异步调用
     * 
     * @param responseClazz
     *            调用待返回的对象类型，T为对象的<tt>Class</tt>类型
     * @param pbrpcMsg
     *            Pbrpc调用的消息对象
     * @return future回调，带有泛型T标示待返回对象类型
     */
    <T extends GeneratedMessage> CallFuture<T> asyncTransport(Class<T> responseClazz,
            PbrpcMsg pbrpcMsg);

    /**
     * 同步调用
     * 
     * @param responseClazz
     *            调用待返回的对象类型，T为对象的<tt>Class</tt>类型
     * @param pbrpcMsg
     *            Pbrpc调用的消息对象
     * @return 调用返回的protobuf对象
     */
    <T extends GeneratedMessage> T syncTransport(Class<T> responseClazz, PbrpcMsg pbrpcMsg);

    /**
     * 返回描述信息
     * 
     * @return 客户端信息
     */
    String getInfo();

}
