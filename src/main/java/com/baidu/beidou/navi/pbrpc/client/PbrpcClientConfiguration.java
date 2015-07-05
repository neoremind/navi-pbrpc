package com.baidu.beidou.navi.pbrpc.client;

import io.netty.channel.ChannelOption;

/**
 * ClassName: PbrpcClientConfiguration <br/>
 * Function: 客户端配置
 * 
 * @author Zhang Xu
 */
public class PbrpcClientConfiguration {

    /**
     * keep alive
     * 
     * @see ChannelOption#SO_KEEPALIVE
     */
    private boolean soKeepalive = true;

    /**
     * reuse addr
     * 
     * @see ChannelOption#SO_REUSEADDR
     */
    private boolean soReuseaddr = true;

    /**
     * tcp nodelay
     * 
     * @see ChannelOption#TCP_NODELAY
     */
    private boolean tcpNodelay = true;

    /**
     * receive buf size
     * 
     * @see ChannelOption#SO_RCVBUF
     */
    private int soRcvbuf = 1024 * 128;

    /**
     * send buf size
     * 
     * @see ChannelOption#SO_SNDBUF
     */
    private int soSndbuf = 1024 * 128;

    public boolean isSoKeepalive() {
        return soKeepalive;
    }

    public void setSoKeepalive(boolean soKeepalive) {
        this.soKeepalive = soKeepalive;
    }

    public boolean isSoReuseaddr() {
        return soReuseaddr;
    }

    public void setSoReuseaddr(boolean soReuseaddr) {
        this.soReuseaddr = soReuseaddr;
    }

    public boolean isTcpNodelay() {
        return tcpNodelay;
    }

    public void setTcpNodelay(boolean tcpNodelay) {
        this.tcpNodelay = tcpNodelay;
    }

    public int getSoRcvbuf() {
        return soRcvbuf;
    }

    public void setSoRcvbuf(int soRcvbuf) {
        this.soRcvbuf = soRcvbuf;
    }

    public int getSoSndbuf() {
        return soSndbuf;
    }

    public void setSoSndbuf(int soSndbuf) {
        this.soSndbuf = soSndbuf;
    }

}
