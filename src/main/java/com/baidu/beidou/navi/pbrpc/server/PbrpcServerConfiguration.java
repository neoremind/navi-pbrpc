package com.baidu.beidou.navi.pbrpc.server;

import io.netty.channel.ChannelOption;

/**
 * ClassName: PbrpcServerConfiguration <br/>
 * Function: 服务端配置
 * 
 * @author Zhang Xu
 */
public class PbrpcServerConfiguration {

    /**
     * keep alive
     * 
     * @see ChannelOption#SO_KEEPALIVE
     */
    private boolean soKeepalive = true;

    /**
     * tcp nodelay
     * 
     * @see ChannelOption#TCP_NODELAY
     */
    private boolean tcpNodelay = true;

    /**
     * so linger
     * 
     * @see ChannelOption#SO_LINGER
     */
    private int soLinger = 2;

    /**
     * so backlog
     * 
     * @see ChannelOption#SO_BACKLOG
     */
    private int soBacklog = 128;

    /**
     * receive buf size
     * 
     * @see ChannelOption#SO_RCVBUF
     */
    private int soRcvbuf = 1024 * 64;

    /**
     * send buf size
     * 
     * @see ChannelOption#SO_SNDBUF
     */
    private int soSndbuf = 1024 * 64;

    public boolean isSoKeepalive() {
        return soKeepalive;
    }

    public void setSoKeepalive(boolean soKeepalive) {
        this.soKeepalive = soKeepalive;
    }

    public boolean isTcpNodelay() {
        return tcpNodelay;
    }

    public void setTcpNodelay(boolean tcpNodelay) {
        this.tcpNodelay = tcpNodelay;
    }

    public int getSoLinger() {
        return soLinger;
    }

    public void setSoLinger(int soLinger) {
        this.soLinger = soLinger;
    }

    public int getSoBacklog() {
        return soBacklog;
    }

    public void setSoBacklog(int soBacklog) {
        this.soBacklog = soBacklog;
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
