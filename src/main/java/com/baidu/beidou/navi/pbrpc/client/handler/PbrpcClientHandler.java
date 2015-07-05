package com.baidu.beidou.navi.pbrpc.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.client.callback.Callback;
import com.baidu.beidou.navi.pbrpc.client.callback.CallbackContext;
import com.baidu.beidou.navi.pbrpc.client.callback.CallbackPool;
import com.baidu.beidou.navi.pbrpc.codec.Codec;
import com.baidu.beidou.navi.pbrpc.codec.impl.ProtobufCodec;
import com.baidu.beidou.navi.pbrpc.error.ExceptionUtil;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMessageDeserializer;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;
import com.baidu.beidou.navi.pbrpc.util.ContextHolder;
import com.baidu.beidou.navi.pbrpc.util.Preconditions;
import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: PbrpcClientHandler <br/>
 * Function: 客户端的核心handler
 * 
 * @author Zhang Xu
 */
public class PbrpcClientHandler extends SimpleChannelInboundHandler<PbrpcMsg> {

    private static final Logger LOG = LoggerFactory.getLogger(PbrpcMessageDeserializer.class);

    /**
     * 可配置，默认使用protobuf来做body的序列化
     */
    private Codec codec = new ProtobufCodec();

    /**
     * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext,
     *      java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void channelRead0(ChannelHandlerContext ctx, PbrpcMsg pbrpcMsg) throws Exception {
        Preconditions
                .checkArgument(pbrpcMsg != null, "Pbrpc msg is null which should never happen");
        try {
            // LOG.info("Got msg from server:" + pbrpcMsg);
            int logId = (int) pbrpcMsg.getLogId();
            CallbackContext context = CallbackPool.getContext(logId);
            if (context == null) {
                LOG.warn("Receive msg from server but no context found, logId=" + logId);
                return;
            }
            Callback<GeneratedMessage> cb = (Callback<GeneratedMessage>) context.getCallback();
            if (pbrpcMsg.getErrorCode() != null) {
                cb.handleError(ExceptionUtil.buildFromErrorCode(pbrpcMsg.getErrorCode()));
            } else {
                GeneratedMessage res = (GeneratedMessage) codec.decode(
                        CallbackPool.getResClass(logId), pbrpcMsg.getData());
                cb.handleResult(res);
            }

            // 短连接则关闭channel
            if (context.isShortAliveConn()) {
                Channel channel = context.getChannel();
                if (channel != null) {
                    LOG.info("Close " + channel + ", logId=" + logId);
                    channel.close();
                }
            }
            // LOG.info("Decoding and invoking callback " + pbrpcMsg.getLogId() + " total "
            // + (System.currentTimeMillis() - context.getStarttime())
            // + "ms, transport using " + (start - context.getStarttime()) + "ms");
        } finally {
            CallbackPool.remove((int) pbrpcMsg.getLogId());
            ContextHolder.clean();
            // ctx.fireChannelReadComplete();
        }
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelReadComplete(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // LOG.debug("Client channelReadComplete>>>>>");
        // ctx.flush();
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // LOG.debug("Client channelActive>>>>>");
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext,
     *      java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error(cause.getMessage(), cause);
        ctx.close(); // FIXME
        // ctx.fireChannelRead();
    }

}
