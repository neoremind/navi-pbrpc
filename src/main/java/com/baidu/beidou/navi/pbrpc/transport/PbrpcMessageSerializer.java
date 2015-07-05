package com.baidu.beidou.navi.pbrpc.transport;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import com.baidu.beidou.navi.pbrpc.error.ErrorCode;
import com.baidu.beidou.navi.pbrpc.protocol.NsHead;
import com.baidu.beidou.navi.pbrpc.util.ByteUtil;
import com.baidu.beidou.navi.pbrpc.util.IdGenerator;
import com.baidu.beidou.navi.pbrpc.util.StringPool;

/**
 * ClassName: PbrpcMessageSerializer <br/>
 * Function: 序列化的Handler
 * 
 * @author Zhang Xu
 */
public class PbrpcMessageSerializer extends MessageToMessageEncoder<PbrpcMsg> {

    // private static final Logger LOG = LoggerFactory.getLogger(PbrpcMessageSerializer.class);

    /**
     * @see io.netty.handler.codec.MessageToMessageEncoder#encode(io.netty.channel.ChannelHandlerContext,
     *      java.lang.Object, java.util.List)
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, PbrpcMsg pbrpcMsg, List<Object> out)
            throws Exception {
        byte[] bodyBytes = ByteUtil.getNonEmptyBytes(pbrpcMsg.getData());
        NsHead nsHead = contructNsHead(pbrpcMsg.getServiceId(), pbrpcMsg.getErrorCode(),
                pbrpcMsg.getLogId(), pbrpcMsg.getData(), pbrpcMsg.getProvider());
        byte[] nsHeadBytes = nsHead.toBytes();
        ByteBuf encoded = Unpooled.copiedBuffer(nsHeadBytes, bodyBytes);
        out.add(encoded);
        // LOG.info("Send total byte size=" + (nsHeadBytes.length + bodyBytes.length) + ", body size="
        // + bodyBytes.length);
    }

    /**
     * 构建NsHead
     * 
     * @param serviceId
     * @param errorCode
     * @param logId
     * @param data
     * @param provider
     * @return NsHead
     */
    private NsHead contructNsHead(int serviceId, ErrorCode errorCode, long logId, byte[] data,
            String provider) {
        NsHead nsHead = new NsHead();
        nsHead.setMethodId(serviceId);
        if (logId != 0L) {
            nsHead.setLogId(logId);
        } else {
            nsHead.setLogId(IdGenerator.genUUID());
        }
        if (errorCode != null) {
            nsHead.setFlags(errorCode.getValue());
        }
        if (data != null) {
            nsHead.setBodyLen(data.length);
        } else {
            nsHead.setBodyLen(0);
        }
        if (provider == null) {
            nsHead.setProvider(StringPool.Symbol.EMPTY);
        } else {
            nsHead.setProvider(provider);
        }
        return nsHead;
    }

}
