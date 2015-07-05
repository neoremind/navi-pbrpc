package com.baidu.beidou.navi.pbrpc.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.codec.Codec;
import com.baidu.beidou.navi.pbrpc.codec.impl.ProtobufCodec;
import com.baidu.beidou.navi.pbrpc.error.ErrorCode;
import com.baidu.beidou.navi.pbrpc.exception.CodecException;
import com.baidu.beidou.navi.pbrpc.exception.ServiceNotFoundException;
import com.baidu.beidou.navi.pbrpc.server.core.ServiceDescriptor;
import com.baidu.beidou.navi.pbrpc.server.core.ServiceLocator;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMessageDeserializer;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;
import com.baidu.beidou.navi.pbrpc.util.ContextHolder;
import com.baidu.beidou.navi.pbrpc.util.Preconditions;
import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: PbrpcServerHandler <br/>
 * Function: 服务端核心的处理handler，内部路由到指定的服务bean并进行调用，返回结果
 * 
 * @author Zhang Xu
 */
public class PbrpcServerHandler extends SimpleChannelInboundHandler<PbrpcMsg> {

    private static final Logger LOG = LoggerFactory.getLogger(PbrpcMessageDeserializer.class);

    /**
     * 可配置，默认使用protobuf来做body的序列化
     */
    private Codec codec = new ProtobufCodec();

    /**
     * 可配置，服务定位器
     */
    private ServiceLocator<Integer> serviceLocator;

    /**
     * Creates a new instance of PbrpcServerHandler.
     * 
     * @param serviceLocator
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public PbrpcServerHandler(ServiceLocator serviceLocator) {
        this.serviceLocator = (ServiceLocator<Integer>) serviceLocator;
    }

    /**
     * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext,
     *      java.lang.Object)
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, PbrpcMsg pbrpcMsg) throws Exception {
        Preconditions
                .checkArgument(pbrpcMsg != null, "Pbrpc msg is null which should never happen");
        try {
            if (pbrpcMsg.getErrorCode() != null) {
                ctx.channel().writeAndFlush(PbrpcMsg.copyLiteOf(pbrpcMsg)); // ONLY COMMUNICATIN ERROR HERE FIXME
                return;
            }
            int key = pbrpcMsg.getServiceId();
            ServiceDescriptor<Integer> servDesc = serviceLocator.getServiceDescriptor(key);
            if (servDesc == null) {
                throw new ServiceNotFoundException(" serviceId=" + pbrpcMsg.getServiceId());
            }

            GeneratedMessage arg = (GeneratedMessage) codec.decode(servDesc.getArgumentClass(),
                    pbrpcMsg.getData());
            GeneratedMessage ret = (GeneratedMessage) servDesc.getMethod().invoke(
                    servDesc.getTarget(), arg);

            PbrpcMsg retMsg = new PbrpcMsg();
            retMsg.setLogId(pbrpcMsg.getLogId());
            retMsg.setServiceId(key);
            if (ret != null && ret instanceof GeneratedMessage) {
                byte[] response = ((GeneratedMessage) ret).toByteArray();
                retMsg.setData(response);
            }
            // LOG.debug("Service biz logic will return:" + ret);
            ctx.channel().writeAndFlush(retMsg);
            // LOG.info(servDesc + " exec using " + (System.currentTimeMillis() - start) + "ms");
        } catch (ServiceNotFoundException e) {
            LOG.error(ErrorCode.SERVICE_NOT_FOUND.getMessage() + e.getMessage(), e);
            ctx.channel().writeAndFlush(
                    PbrpcMsg.copyLiteOf(pbrpcMsg).setErrorCode(ErrorCode.SERVICE_NOT_FOUND));
        } catch (CodecException e) {
            LOG.error(ErrorCode.PROTOBUF_CODEC_ERROR.getMessage() + e.getMessage(), e);
            ctx.channel().writeAndFlush(
                    PbrpcMsg.copyLiteOf(pbrpcMsg).setErrorCode(ErrorCode.PROTOBUF_CODEC_ERROR));
        } catch (InvocationTargetException e) {
            LOG.error(ErrorCode.INVOCATION_TARGET_EXCEPTION.getMessage() + e.getMessage(), e);
            ctx.channel().writeAndFlush(
                    PbrpcMsg.copyLiteOf(pbrpcMsg).setErrorCode(
                            ErrorCode.INVOCATION_TARGET_EXCEPTION));
        } catch (Exception e) {
            LOG.error(ErrorCode.UNEXPECTED_ERROR.getMessage() + e.getMessage(), e);
            ctx.channel().writeAndFlush(
                    PbrpcMsg.copyLiteOf(pbrpcMsg).setErrorCode(ErrorCode.UNEXPECTED_ERROR));
        } finally {
            ContextHolder.clean();
        }
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelReadComplete(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext,
     *      java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error(cause.getCause().getMessage(), cause.getCause());

        long logId = (Long) (ContextHolder.getContext("_logid"));
        PbrpcMsg retMsg = new PbrpcMsg();
        retMsg.setLogId(logId);
        retMsg.setErrorCode(ErrorCode.COMMUNICATION_ERROR);
        // ctx.channel().writeAndFlush(retMsg);
        ctx.fireChannelRead(retMsg); // FIXME 对于通信异常的这样处理是否OK？
    }

}
