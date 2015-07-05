package com.baidu.beidou.navi.pbrpc.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.baidu.beidou.navi.pbrpc.protocol.Header;
import com.baidu.beidou.navi.pbrpc.protocol.NsHead;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;
import com.baidu.beidou.navi.pbrpc.util.ByteUtil;
import com.baidu.beidou.navi.pbrpc.util.IdGenerator;

/**
 * ClassName: NsHeaderResolver <br/>
 * Function: NsHead头解析构造器
 * 
 * @author Zhang Xu
 */
public class NsHeaderResolver implements HeaderResolver {

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.HeaderResolver#resolveResHeader(java.io.InputStream)
     */
    @Override
    public Header resolveResHeader(InputStream in) throws IOException {
        NsHead nsHead = new NsHead();
        byte[] nsHeadBytes = new byte[nsHead.getFixedHeaderLen()];
        ByteUtil.read(nsHeadBytes, in);
        nsHead.wrap(nsHeadBytes);
        return nsHead;
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.HeaderResolver#resolveResBodyByResHeader(com.baidu.beidou.navi.pbrpc.protocol.Header,
     *      java.io.InputStream)
     */
    @Override
    public byte[] resolveResBodyByResHeader(Header header, InputStream in) throws IOException {
        byte[] bodyBytes = new byte[(int) header.getBodyLen()];
        ByteUtil.read(bodyBytes, in);
        return bodyBytes;
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.HeaderResolver#packReqHeaderAndBody(com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg)
     */
    @Override
    public byte[] packReqHeaderAndBody(PbrpcMsg pbrpcMsg) throws IOException {
        NsHead nsHead = new NsHead();
        nsHead.setLogId(IdGenerator.genUUID());
        nsHead.setMethodId(pbrpcMsg.getServiceId());
        nsHead.setProvider(pbrpcMsg.getProvider());
        nsHead.setBodyLen(pbrpcMsg.getData().length);
        byte[] headerBytes = nsHead.toBytes();

        ByteBuffer buffer = ByteBuffer.allocate(nsHead.getFixedHeaderLen()
                + pbrpcMsg.getData().length);
        buffer.put(headerBytes).put(pbrpcMsg.getData());
        buffer.flip();

        byte[] packet = new byte[buffer.remaining()];
        buffer.get(packet);

        return packet;
    }

}
