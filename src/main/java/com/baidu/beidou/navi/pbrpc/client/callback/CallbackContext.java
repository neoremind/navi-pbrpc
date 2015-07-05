package com.baidu.beidou.navi.pbrpc.client.callback;

import io.netty.channel.Channel;

import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: CallbackContext <br/>
 * Function: 回调上下文，用于定位回调，计算超时、关闭channel等辅助功能
 * 
 * @author Zhang Xu
 */
public class CallbackContext {

    /**
     * 用于标示某个回调的id，用<code>NsHead</code>头中的<tt>logId</tt>来标示
     */
    private final int logId;

    /**
     * 调用起始时间
     */
    private final long startTime;

    /**
     * 客户端调用是否为短连接
     */
    private boolean isShortAliveConn;

    /**
     * 客户端调用用的channel，如果是长连接可以为空，只有配合{@link #isShortAliveConn}为true时候，才会再回调中关闭
     */
    private Channel channel;

    /**
     * 调用结束时间
     */
    private final int timeout;

    /**
     * 期望服务返回的pb类型
     */
    private final Class<? extends GeneratedMessage> resClazz;

    /**
     * 回调
     */
    private final Callback<? extends GeneratedMessage> callback;

    /**
     * Creates a new instance of CallbackContext.
     * 
     * @param logId
     * @param startTime
     * @param timeout
     * @param isShortAliveConn
     * @param channel
     * @param resClazz
     * @param callback
     */
    public CallbackContext(int logId, long startTime, int timeout, boolean isShortAliveConn,
            Channel channel, Class<? extends GeneratedMessage> resClazz,
            Callback<? extends GeneratedMessage> callback) {
        super();
        this.logId = logId;
        this.startTime = startTime;
        this.timeout = timeout;
        this.isShortAliveConn = isShortAliveConn;
        this.channel = channel;
        this.resClazz = resClazz;
        this.callback = callback;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getLogId() {
        return logId;
    }

    public Class<? extends GeneratedMessage> getResClazz() {
        return resClazz;
    }

    public Callback<? extends GeneratedMessage> getCallback() {
        return callback;
    }

    public boolean isShortAliveConn() {
        return isShortAliveConn;
    }

    public void setShortAliveConn(boolean isShortAliveConn) {
        this.isShortAliveConn = isShortAliveConn;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

}
