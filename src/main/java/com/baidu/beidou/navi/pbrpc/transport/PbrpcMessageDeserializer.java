package com.baidu.beidou.navi.pbrpc.transport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import com.baidu.beidou.navi.pbrpc.protocol.NsHead;
import com.baidu.beidou.navi.pbrpc.util.ContextHolder;

/**
 * ClassName: PbrpcMessageDeserializer <br/>
 * Function: 反序列化handler
 * 
 * @author Zhang Xu
 */
public class PbrpcMessageDeserializer extends ByteToMessageDecoder {

    // private static final Logger LOG = LoggerFactory.getLogger(PbrpcMessageDeserializer.class);

    /**
     * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext,
     *      io.netty.buffer.ByteBuf, java.util.List)
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 解决半包问题，此时Nshead还没有接收全，channel中留存的字节流不做处理
        if (in.readableBytes() < NsHead.NSHEAD_LEN) {
            return;
        }

        in.markReaderIndex();

        byte[] bytes = new byte[NsHead.NSHEAD_LEN];
        in.readBytes(bytes, 0, NsHead.NSHEAD_LEN);

        NsHead nsHead = new NsHead();
        nsHead.wrap(bytes);

        // 解决半包问题，此时body还没有接收全，channel中留存的字节流不做处理，重置readerIndex
        if (in.readableBytes() < (int) nsHead.getBodyLen()) {
            in.resetReaderIndex();
            return;
        }

        // 此时接受到了足够的一个包，开始处理
        in.markReaderIndex();

        byte[] totalBytes = new byte[(int) nsHead.getBodyLen()];
        in.readBytes(totalBytes, 0, (int) nsHead.getBodyLen());

        PbrpcMsg decoded = PbrpcMsg.of(nsHead).setData(totalBytes);
        ContextHolder.putContext("_logid", nsHead.getLogId()); // TODO

        if (decoded != null) {
            out.add(decoded);
        }
        // LOG.info("Deser data " + nsHead.getLogId() + " is" + decoded + " and using "
        // + (System.nanoTime() - start) / 1000 + "us");
    }
}
