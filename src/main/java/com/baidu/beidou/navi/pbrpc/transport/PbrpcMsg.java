package com.baidu.beidou.navi.pbrpc.transport;

import com.baidu.beidou.navi.pbrpc.error.ErrorCode;
import com.baidu.beidou.navi.pbrpc.protocol.NsHead;

/**
 * ClassName: PbrpcMsg <br/>
 * Function: 使用Protobuf做序列化协议的消息VO类，仅用于服务端或者客户端的内存中使用，不会真正用于序列化使用
 * <p>
 * 一个传输包包含的Header+body两部分，目前header采用<code>NsHead</code>来做，body采用Protobuf协议序列化的字节码
 * <P>
 * 示意如下： 该类作为服务端和客户端的在内存中使用的VO类，包含了一些最基本的信息，服务端路由的service标示，客户端调用来的logId用于tracing使用，以及最基本的序列化后的数据。
 * 
 * <pre>
 *       Byte/     0       |       1       |       2       |       3       |
 *          /              |               |               |               |
 *         |0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|
 *         +---------------+---------------+---------------+---------------+
 *        0/ NSHEAD HEADER                                                 /
 *         /                                                               /
 *         /                                                               /
 *         /                                                               /
 *         +---------------+---------------+---------------+---------------+
 *       36/ serialized protobuf message                                   /
 *        +/  (note length in the header body length field)                /
 *         +---------------+---------------+---------------+---------------+
 * </pre>
 * 
 * @author Zhang Xu
 */
public class PbrpcMsg {

    /**
     * 服务的标识id，一般客户端需要制定该id，服务端可以利用这个id路由到某个方法上调用。<br/>
     * 实际就是{@link NsHead}头中<tt>methodId</tt>，用于在服务端和客户端传递
     */
    private int serviceId;

    /**
     * 服务上下文logId，用于tracing使用。 <br/>
     * 实际就是{@link NsHead}头中<tt>logId</tt>，用于在服务端和客户端传递
     */
    private long logId;

    /**
     * 调用提供者，类似于appid。<br/>
     * 实际就是{@link NsHead}头中<tt>provider</tt>，用于在服务端和客户端传递
     */
    private String provider;

    /**
     * 一些不关业务逻辑处理的errorCode，由框架负责处理标示，发送请求时候请无设置该值
     */
    private ErrorCode errorCode;

    /**
     * 传输的经过protobuf序列化的字节码
     */
    private byte[] data;

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PbrpcMsg[logId=");
        sb.append(logId);
        sb.append(", serviceId=");
        sb.append(serviceId);
        sb.append(", provider=");
        sb.append(provider);
        sb.append(", dataLength=");
        sb.append((data == null) ? 0 : data.length);
        if (errorCode != null) {
            sb.append(", errorCode=");
            sb.append(errorCode);
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 根据NsHead构造
     * 
     * @param nsHead
     * @return
     */
    public static PbrpcMsg of(NsHead nsHead) {
        PbrpcMsg ret = new PbrpcMsg();
        ret.setServiceId((int) nsHead.getMethodId());
        ret.setLogId(nsHead.getLogId());
        ret.setErrorCode(ErrorCode.get(nsHead.getFlags()));
        ret.setProvider(nsHead.getProvider());
        return ret;
    }

    /**
     * 简单信息的拷贝复制，不拷贝字节码
     * 
     * @param msg
     * @return
     */
    public static PbrpcMsg copyLiteOf(PbrpcMsg msg) {
        PbrpcMsg ret = new PbrpcMsg();
        ret.setLogId(msg.getLogId());
        ret.setProvider(msg.getProvider());
        ret.setServiceId(msg.getServiceId());
        ret.setErrorCode(msg.getErrorCode());
        return ret;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public PbrpcMsg setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public int getServiceId() {
        return serviceId;
    }

    public PbrpcMsg setServiceId(int serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public PbrpcMsg setData(byte[] data) {
        this.data = data;
        return this;
    }

    public long getLogId() {
        return logId;
    }

    public PbrpcMsg setLogId(long logId) {
        this.logId = logId;
        return this;
    }

    public String getProvider() {
        return provider;
    }

    public PbrpcMsg setProvider(String provider) {
        this.provider = provider;
        return this;
    }

}
