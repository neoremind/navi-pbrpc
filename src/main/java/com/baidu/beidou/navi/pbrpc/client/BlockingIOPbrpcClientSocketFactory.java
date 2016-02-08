package com.baidu.beidou.navi.pbrpc.client;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.codec.Codec;

/**
 * ClassName: BlockingIOPbrpcClientSocketFactory <br/>
 * Function: 连接池对象构造工厂
 * 
 * @author Zhang Xu
 */
public class BlockingIOPbrpcClientSocketFactory extends BasePoolableObjectFactory {

    private static final Logger LOG = LoggerFactory
            .getLogger(BlockingIOPbrpcClientSocketFactory.class);

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
     * 客户端配置
     */
    private PbrpcClientConfiguration pbrpcClientConfiguration;

    /**
     * 可配置，默认使用protobuf来做body的序列化
     */
    private Codec codec;

    /**
     * header+body通讯协议方式的头解析构造器
     */
    private HeaderResolver headerResolver;

    /**
     * Creates a new instance of BlockingIOPbrpcClientSocketFactory.
     * 
     * @param ip
     * @param port
     * @param connTimeout
     * @param readTimeout
     * @param codec
     * @param headerResolver
     */
    public BlockingIOPbrpcClientSocketFactory(PbrpcClientConfiguration pbrpcClientConfiguration,
            String ip, int port, int connTimeout, int readTimeout, Codec codec,
            HeaderResolver headerResolver) {
        if (pbrpcClientConfiguration == null) {
            this.pbrpcClientConfiguration = new PbrpcClientConfiguration();
        }
        this.ip = ip;
        this.port = port;
        this.connTimeout = connTimeout;
        this.readTimeout = readTimeout;
        this.codec = codec;
        this.headerResolver = headerResolver;
    }

    /**
     * @see org.apache.commons.pool.BasePoolableObjectFactory#makeObject()
     */
    public Object makeObject() throws Exception {
        BlockingIOPbrpcClient pbrpcClient = new BlockingIOPbrpcClient(pbrpcClientConfiguration,
                false, ip, port, connTimeout, readTimeout);
        pbrpcClient.setCodec(this.codec);
        pbrpcClient.setHeaderResolver(headerResolver);

        LOG.info("Making new blocking io connection on " + pbrpcClient.getInfo()
                + " and adding to pool done");

        return pbrpcClient;
    }

    /**
     * @see org.apache.commons.pool.BasePoolableObjectFactory#destroyObject(java.lang.Object)
     */
    public void destroyObject(final Object obj) throws Exception {
        if (obj instanceof BlockingIOPbrpcClient) {
            final BlockingIOPbrpcClient pbrpcClient = (BlockingIOPbrpcClient) obj;
            Socket socket = pbrpcClient.getSocket();
            if (socket != null) {
                try {
                    if (socket.getInputStream() != null) {
                        try {
                            socket.getInputStream().close();
                        } catch (Exception e) {
                            LOG.warn("Failed to close input stream, " + e.getMessage(), e);
                        }
                    }

                    if (socket.getOutputStream() != null) {
                        try {
                            socket.getOutputStream().close();
                        } catch (Exception e) {
                            LOG.warn("Failed to close output stream, " + e.getMessage(), e);
                        }
                    }

                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (Exception e) {
                            LOG.warn("Failed to close socket, " + e.getMessage(), e);
                        }
                    }
                } catch (Exception e) {
                    // omit exception here
                }
            }
            LOG.info("Closing socket and destroy blocking io connection from pool done");
        }
    }

    /**
     * @see org.apache.commons.pool.BasePoolableObjectFactory#validateObject(java.lang.Object)
     */
    public boolean validateObject(Object obj) {
        if (obj instanceof BlockingIOPbrpcClient) {
            final BlockingIOPbrpcClient pbrpcClient = (BlockingIOPbrpcClient) obj;
            Socket socket = pbrpcClient.getSocket();
            if (socket == null) {
                return true; // first get so that's true here
            } else {
                int soTimeout = 0; // TODO HERE
                try {
                    soTimeout = socket.getSoTimeout();
                    socket.setSoTimeout(1);
                    PushbackInputStream pbin = new PushbackInputStream(socket.getInputStream());
                    //PushbackInputStream pbin = (PushbackInputStream) socket.getInputStream();
                    int test = pbin.read();
                    if (test == -1) {
                        return false;
                    }
                    pbin.unread(test);
                    return true;
                } catch (SocketTimeoutException e) {
                    return true;
                } catch (IOException e) {
                    return false;
                } finally {
                    try {
                        if (soTimeout != 0) {
                            socket.setSoTimeout(soTimeout);
                        }
                    } catch (SocketException e) {
                        // omit exception
                    }
                }
            }
        }
        return false;
    }

}
