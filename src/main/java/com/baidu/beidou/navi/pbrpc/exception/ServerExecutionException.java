package com.baidu.beidou.navi.pbrpc.exception;

/**
 * ClassName: ServerExecutionException <br/>
 * Function: 服务端执行异常，当通过反射调用本地方法时抛出异常或者发生其他未知异常抛出
 * 
 * @author Zhang Xu
 */
public class ServerExecutionException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5196421433506179782L;

    /**
     * Creates a new instance of ServerExecutionException.
     */
    public ServerExecutionException() {
        super();
    }

    /**
     * Creates a new instance of ServerExecutionException.
     * 
     * @param arg0
     * @param arg1
     */
    public ServerExecutionException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Creates a new instance of ServerExecutionException.
     * 
     * @param arg0
     */
    public ServerExecutionException(String arg0) {
        super(arg0);
    }

    /**
     * Creates a new instance of ServerExecutionException.
     * 
     * @param arg0
     */
    public ServerExecutionException(Throwable arg0) {
        super(arg0);
    }

}
