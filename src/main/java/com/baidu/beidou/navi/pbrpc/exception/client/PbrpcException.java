package com.baidu.beidou.navi.pbrpc.exception.client;

/**
 * ClassName: PbrpcException <br/>
 * Function: 客户端通用的关于Pbprc的异常
 * 
 * @author Zhang Xu
 */
public class PbrpcException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5196421433506179782L;

    /**
     * Creates a new instance of PbrpcException.
     */
    public PbrpcException() {
        super();
    }

    /**
     * Creates a new instance of PbrpcException.
     * 
     * @param arg0
     * @param arg1
     */
    public PbrpcException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Creates a new instance of PbrpcException.
     * 
     * @param arg0
     */
    public PbrpcException(String arg0) {
        super(arg0);
    }

    /**
     * Creates a new instance of PbrpcException.
     * 
     * @param arg0
     */
    public PbrpcException(Throwable arg0) {
        super(arg0);
    }

}
