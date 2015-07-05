package com.baidu.beidou.navi.pbrpc.protocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.baidu.beidou.navi.pbrpc.util.ByteUtil;
import com.baidu.beidou.navi.pbrpc.util.UnsignedSwitch;

/**
 * ClassName: NsHead <br/>
 * Function: Nshead协议头 <br/>
 * 协议头长度为36个字节，详细信息如下：
 * 
 * <pre>
 *       Byte/     0       |       1       |       2       |       3       |
 *          /              |               |               |               |
 *         |0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|
 *         +---------------+---------------+---------------+---------------+
 *        0| id                            | flags                         |
 *         +---------------+---------------+---------------+---------------+
 *        4| log id                                                        |
 *         +---------------+---------------+---------------+---------------+
 *        8| provider                                                      |
 *         +                                                               +
 *       12|                                                               |
 *         +                                                               +
 *       16|                                                               |
 *         +                                                               +
 *       20|                                                               |
 *         +---------------+---------------+---------------+---------------+
 *       24| magic number                                                  |
 *         +---------------+---------------+---------------+---------------+
 *       28| method id                                                     |
 *         +---------------+---------------+---------------+---------------+
 *       32| body length                                                   |
 *         +---------------+---------------+---------------+---------------+
 *         Total 36 bytes
 * </pre>
 * 
 * @author Zhang Xu
 */
public class NsHead implements Header {

    /**
     * NsHead头大小
     */
    public static final int NSHEAD_LEN = 36;

    /**
     * unsigned short(2) 默认为0，暂时无用
     */
    protected int id;

    /**
     * unsigned short(2) 默认为0，暂时无用
     */
    protected int flags;

    /**
     * unsigned int(4) 随机生成的追踪id，一般客户端调用时候赋值，服务端的响应会回写该id，用于上下文一致性的需求，类似于traceId
     */
    protected long logId;

    /**
     * char(16)，表示调用方的标示，相当于appid概念
     */
    protected String provider;

    /**
     * unsigned int(4) 特殊标识：常数 0xfb709394，标识一个包的起始
     */
    private long magicNum = 0xfb709394;

    /**
     * 对于一个方法的id，用于服务的路由
     */
    protected long methodId;

    /**
     * unsigned int(4) NsHead后数据的总字节长度
     */
    protected long bodyLen;

    /**
     * 转为字节流 <br/>
     * 使用小尾端来进行转换
     * 
     * @return
     * @throws RuntimeException
     */
    @Override
    public byte[] toBytes() throws RuntimeException {
        ByteBuffer bb = ByteBuffer.allocate(NSHEAD_LEN);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        try {
            bb.putShort(UnsignedSwitch.intToUshort(id));
            bb.putShort(UnsignedSwitch.intToUshort(flags));
            bb.putInt(UnsignedSwitch.longToUint(logId));
            byte[] prvd = ByteUtil.convertStringToBytes(provider, 16);
            bb.put(prvd);
            bb.putInt(UnsignedSwitch.longToUint(magicNum));
            bb.putInt(UnsignedSwitch.longToUint(methodId));
            bb.putInt(UnsignedSwitch.longToUint(bodyLen));
        } catch (Exception e) {
            throw new RuntimeException("Nshead to byte[] failed", e);
        }

        return bb.array();
    }

    /**
     * Creates a new instance of NsHead.
     * 
     * @param input
     */
    public NsHead(byte[] input) {
        wrap(input);
    }

    /**
     * Creates a new instance of NsHead.
     */
    public NsHead() {

    }

    /**
     * 从字节流还原Nshead头
     * 
     * @param input
     */
    @Override
    public void wrap(byte[] input) {
        ByteBuffer bb = ByteBuffer.allocate(NSHEAD_LEN);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(input);
        bb.flip();
        id = UnsignedSwitch.uShortToInt(bb.getShort());
        flags = UnsignedSwitch.uShortToInt(bb.getShort());
        logId = UnsignedSwitch.uintToLong(bb.getInt());
        byte[] bf = new byte[16];
        bb.get(bf);
        provider = ByteUtil.convertBytesToString(bf);
        magicNum = UnsignedSwitch.uintToLong(bb.getInt());
        methodId = UnsignedSwitch.uintToLong(bb.getInt());
        bodyLen = UnsignedSwitch.uintToLong(bb.getInt());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public long getMagicNum() {
        return magicNum;
    }

    public void setMagicNum(long magicNum) {
        this.magicNum = magicNum;
    }

    public long getMethodId() {
        return methodId;
    }

    public void setMethodId(long methodId) {
        this.methodId = methodId;
    }

    @Override
    public long getBodyLen() {
        return bodyLen;
    }

    @Override
    public void setBodyLen(long bodyLen) {
        this.bodyLen = bodyLen;
    }

    @Override
    public int getFixedHeaderLen() {
        return NSHEAD_LEN;
    }

}
