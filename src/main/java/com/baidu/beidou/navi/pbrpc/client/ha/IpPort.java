package com.baidu.beidou.navi.pbrpc.client.ha;

/**
 * ClassName: IpPort <br/>
 * Function: IP端口类
 * 
 * @author Zhang Xu
 */
public class IpPort {

    /**
     * ip
     */
    private String ip;

    /**
     * 端口
     */
    private int port;

    /**
     * Creates a new instance of IpPort.
     * 
     * @param ip
     * @param port
     */
    public IpPort(String ip, int port) {
        super();
        this.ip = ip;
        this.port = port;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ip + ":" + port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
