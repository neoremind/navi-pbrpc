package com.baidu.beidou.navi.pbrpc.exception;

/**
 * ClassName: ServiceNotFoundException <br/>
 * Function: 服务端暴露未找到服务异常
 * 
 * @author Zhang Xu
 */
public class ServiceNotFoundException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5196421433506179782L;

    /**
     * Creates a new instance of ServiceNotFoundException.
     */
    public ServiceNotFoundException() {
        super();
    }

    /**
     * Creates a new instance of ServiceNotFoundException.
     * 
     * @param arg0
     * @param arg1
     */
    public ServiceNotFoundException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Creates a new instance of ServiceNotFoundException.
     * 
     * @param arg0
     */
    public ServiceNotFoundException(String arg0) {
        super(arg0);
    }

    /**
     * Creates a new instance of ServiceNotFoundException.
     * 
     * @param arg0
     */
    public ServiceNotFoundException(Throwable arg0) {
        super(arg0);
    }

}
