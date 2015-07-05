package com.baidu.beidou.navi.pbrpc.transport.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassName: RpcServerChannelIdleHandler <br/>
 * Function: 处理一些空闲的连接闭关它们，防止占用服务端资源
 * 
 * @author Zhang Xu
 */
public class RpcServerChannelIdleHandler extends ChannelDuplexHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RpcServerChannelIdleHandler.class);

    /**
     * Creates a new instance of RpcServerChannelIdleHandler.
     */
    public RpcServerChannelIdleHandler() {

    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#userEventTriggered(io.netty.channel.ChannelHandlerContext,
     *      java.lang.Object)
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.WRITER_IDLE) {
                LOG.warn("Write idle on channel:" + ctx.channel() + " is timeout");
            } else if (e.state() == IdleState.READER_IDLE) {
                LOG.warn("Read idle on channel:" + ctx.channel() + " is timeout on "
                        + ctx.channel().remoteAddress() + ", so close it");
                // ctx.fireExceptionCaught(ReadTimeoutException.INSTANCE);
                ctx.close();
            }
        }
    }

}
