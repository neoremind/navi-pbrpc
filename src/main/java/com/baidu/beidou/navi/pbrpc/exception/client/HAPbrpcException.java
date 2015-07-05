package com.baidu.beidou.navi.pbrpc.exception.client;

/**
 * ClassName: HAPbrpcException <br/>
 * Function: 高可用的客户端异常
 * 
 * @author Zhang Xu
 */
public class HAPbrpcException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5196421433506179782L;

    /**
     * Creates a new instance of HAPbrpcException.
     */
    public HAPbrpcException() {
        super();
    }

    /**
     * Creates a new instance of HAPbrpcException.
     * 
     * @param arg0
     * @param arg1
     */
    public HAPbrpcException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Creates a new instance of HAPbrpcException.
     * 
     * @param arg0
     */
    public HAPbrpcException(String arg0) {
        super(arg0);
    }

    /**
     * Creates a new instance of HAPbrpcException.
     * 
     * @param arg0
     */
    public HAPbrpcException(Throwable arg0) {
        super(arg0);
    }

}
