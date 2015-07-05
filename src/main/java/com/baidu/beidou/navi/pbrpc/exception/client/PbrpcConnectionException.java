package com.baidu.beidou.navi.pbrpc.exception.client;

/**
 * ClassName: PbrpcConnectionException <br/>
 * Function: 客户端连接服务端是否发生的连接异常
 * 
 * @author Zhang Xu
 */
public class PbrpcConnectionException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5196421433506179782L;

    /**
     * Creates a new instance of PbrpcConnectionException.
     */
    public PbrpcConnectionException() {
        super();
    }

    /**
     * Creates a new instance of PbrpcConnectionException.
     * 
     * @param arg0
     * @param arg1
     */
    public PbrpcConnectionException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Creates a new instance of PbrpcConnectionException.
     * 
     * @param arg0
     */
    public PbrpcConnectionException(String arg0) {
        super(arg0);
    }

    /**
     * Creates a new instance of PbrpcConnectionException.
     * 
     * @param arg0
     */
    public PbrpcConnectionException(Throwable arg0) {
        super(arg0);
    }

}
