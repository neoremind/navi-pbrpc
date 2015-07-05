package com.baidu.beidou.navi.pbrpc.client;

import io.netty.channel.ChannelFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.client.callback.CallFuture;
import com.baidu.beidou.navi.pbrpc.client.callback.CallbackPool;
import com.baidu.beidou.navi.pbrpc.exception.client.PbrpcException;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;
import com.baidu.beidou.navi.pbrpc.util.IdGenerator;
import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: PbrpcClientChannel <br/>
 * Function: 客户端连接池内的对象封装
 * 
 * @author Zhang Xu
 */
public class PbrpcClientChannel {

    private static final Logger LOG = LoggerFactory.getLogger(PbrpcClientChannel.class);

    /**
     * netty连接channel的future引用
     */
    private ChannelFuture channelFuture;

    /**
     * 异步调用
     * 
     * @param responseClazz
     * @param pbrpcMsg
     * @param readTimeout
     *            客户端调用超时时间
     * @return
     * @throws Exception
     */
    public <T extends GeneratedMessage> CallFuture<T> asyncTransport(Class<T> responseClazz,
            PbrpcMsg pbrpcMsg, int readTimeout) throws Exception {
        if (channelFuture != null) {
            try {
                int uuid = IdGenerator.genUUID();
                pbrpcMsg.setLogId(uuid);
                CallFuture<T> future = CallFuture.newInstance();
                CallbackPool.put(uuid, readTimeout, false, null, responseClazz, future);
                // long start = System.currentTimeMillis();
                channelFuture.channel().writeAndFlush(pbrpcMsg);
                // LOG.info("Send message " + pbrpcMsg + " done using " + (System.currentTimeMillis() - start) + "ms");
                return future;
            } catch (Exception e) {
                LOG.error(
                        "Failed to transport to " + channelFuture.channel() + " due to "
                                + e.getMessage(), e);
                throw new PbrpcException(e);
            }
        } else {
            LOG.error("Socket channel is not well established, so failed to transport");
            throw new PbrpcException(
                    "ChannelFuture is null! Socket channel is not well established, so failed to transport");
        }

    }

    /**
     * 同步调用
     * 
     * @param responseClazz
     * @param pbrpcMsg
     * @param readTimeout
     *            客户端调用超时时间
     * @return
     * @throws Exception
     */
    public <T extends GeneratedMessage> T syncTransport(Class<T> responseClazz, PbrpcMsg pbrpcMsg,
            int readTimeout) throws Exception {
        return asyncTransport(responseClazz, pbrpcMsg, readTimeout).get();
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public void setChannelFuture(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

}
