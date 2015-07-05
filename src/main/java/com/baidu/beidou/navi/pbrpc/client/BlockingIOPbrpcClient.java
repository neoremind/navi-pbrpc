package com.baidu.beidou.navi.pbrpc.client;

import io.netty.channel.ChannelFuture;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.client.callback.CallFuture;
import com.baidu.beidou.navi.pbrpc.codec.Codec;
import com.baidu.beidou.navi.pbrpc.codec.impl.ProtobufCodec;
import com.baidu.beidou.navi.pbrpc.exception.client.OperationNotSupportException;
import com.baidu.beidou.navi.pbrpc.exception.client.PbrpcConnectionException;
import com.baidu.beidou.navi.pbrpc.exception.client.PbrpcException;
import com.baidu.beidou.navi.pbrpc.protocol.Header;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;
import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: BlockingIOPbrpcClient <br/>
 * Function: 简单的远程访问客户端，使用短连接Blocking IO方式调用服务端，不使用nio
 * 
 * @author Zhang Xu
 */
public class BlockingIOPbrpcClient implements PbrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(BlockingIOPbrpcClient.class);

    /**
     * 远程服务ip地址
     */
    private String ip;

    /**
     * 远程服务端口
     */
    private int port;

    /**
     * 客户端连接超时，单位毫秒
     */
    private int connTimeout;

    /**
     * 客户端调用超时，单位毫秒
     */
    private int readTimeout;

    /**
     * 是否为短连接调用
     */
    private boolean isShortAliveConn;

    /**
     * 长连接调用会使用的
     */
    private Socket socket;

    /**
     * 客户端配置
     */
    private PbrpcClientConfiguration pbrpcClientConfiguration = new PbrpcClientConfiguration();

    /**
     * 可配置，默认使用protobuf来做body的序列化
     */
    private Codec codec = new ProtobufCodec();

    /**
     * header+body通讯协议方式的头解析构造器
     */
    private HeaderResolver headerResolver = new NsHeaderResolver();

    /**
     * Creates a new instance of BlockingIOPbrpcClient.
     * 
     * @param pbrpcClientConfiguration
     * @param isShortAliveConnection
     * @param ip
     * @param port
     * @param connTimeout
     * @param readTimeout
     */
    BlockingIOPbrpcClient(PbrpcClientConfiguration pbrpcClientConfiguration,
            boolean isShortAliveConnection, String ip, int port, int connTimeout, int readTimeout) {
        if (pbrpcClientConfiguration != null) {
            this.pbrpcClientConfiguration = pbrpcClientConfiguration;
        }
        this.isShortAliveConn = isShortAliveConnection;
        this.ip = ip;
        this.port = port;
        this.connTimeout = connTimeout;
        this.readTimeout = readTimeout;
    }

    /**
     * Creates a new instance of BlockingIOPbrpcClient.
     * 
     * @param ip
     * @param port
     * @param connTimeout
     * @param readTimeout
     */
    public BlockingIOPbrpcClient(String ip, int port, int connTimeout, int readTimeout) {
        this(null, true, ip, port, connTimeout, readTimeout);
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#connect()
     */
    public ChannelFuture connect() {
        throw new OperationNotSupportException();
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#asyncTransport(java.lang.Class,
     *      com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg)
     */
    public <T extends GeneratedMessage> CallFuture<T> asyncTransport(Class<T> responseClazz,
            PbrpcMsg pbrpcMsg) {
        throw new OperationNotSupportException();
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#syncTransport(java.lang.Class,
     *      com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg)
     */
    @SuppressWarnings("unchecked")
    public <T extends GeneratedMessage> T syncTransport(Class<T> responseClazz, PbrpcMsg pbrpcMsg) {
        Socket socket = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            if (isShortAliveConn) {
                socket = buildSocket();
            } else {
                if (this.socket == null) {
                    // by default only pooling is allowd to access long alive connection
                    this.socket = buildSocket();
                }
                socket = this.socket;
            }

            socket.connect(new InetSocketAddress(this.ip, this.port), this.connTimeout);
            out = socket.getOutputStream();
            in = socket.getInputStream();

            byte[] body = headerResolver.packReqHeaderAndBody(pbrpcMsg);
            if (body != null && body.length > 0) {
                out.write(body);
                out.flush();
            }

            Header header = headerResolver.resolveResHeader(in);
            body = headerResolver.resolveResBodyByResHeader(header, in);
            return (T) (codec.decode(responseClazz, body));
        } catch (SocketTimeoutException e) {
            LOG.error("Failed to connect on " + getInfo() + " due to " + e.getMessage(), e);
            throw new PbrpcConnectionException(e);
        } catch (Exception e) {
            LOG.error("Failed to do transport on " + getInfo() + " due to " + e.getMessage(), e);
            throw new PbrpcException(e);
        } finally {
            if (isShortAliveConn) {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e) {
                        LOG.info("Failed to close input stream, " + e.getMessage(), e);
                    }
                }

                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e) {
                        LOG.info("Failed to close output stream, " + e.getMessage(), e);
                    }
                }

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        LOG.info("Failed to close socket, " + e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#shutdown()
     */
    public void shutdown() {

    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.PbrpcClient#getInfo()
     */
    @Override
    public String getInfo() {
        return ip + ":" + port + ", connTimeout=" + this.connTimeout + ", readTimeout="
                + this.readTimeout;
    }

    /**
     * 构造一个socket
     * 
     * @return
     * @throws SocketException
     */
    private Socket buildSocket() throws SocketException {
        Socket socket = new Socket();
        socket.setKeepAlive(isShortAliveConn ? false : true);
        socket.setReuseAddress(pbrpcClientConfiguration.isSoReuseaddr());
        socket.setSendBufferSize(pbrpcClientConfiguration.getSoSndbuf());
        socket.setReceiveBufferSize(pbrpcClientConfiguration.getSoRcvbuf());
        socket.setTcpNoDelay(pbrpcClientConfiguration.isTcpNodelay());
        socket.setSoTimeout(this.readTimeout);
        return socket;
    }

    /**
     * 返回socket
     * 
     * @return
     */
    public Socket getSocket() {
        return socket;
    }

    public void setCodec(Codec codec) {
        this.codec = codec;
    }

    public void setHeaderResolver(HeaderResolver headerResolver) {
        this.headerResolver = headerResolver;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setConnTimeout(int connTimeout) {
        this.connTimeout = connTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setPbrpcClientConfiguration(PbrpcClientConfiguration pbrpcClientConfiguration) {
        this.pbrpcClientConfiguration = pbrpcClientConfiguration;
    }

}
