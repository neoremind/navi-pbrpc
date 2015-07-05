package com.baidu.beidou.navi.pbrpc.exception.client;

/**
 * ClassName: OperationNotSupportException <br/>
 * Function: 不允许操作异常
 * 
 * @author Zhang Xu
 */
public class OperationNotSupportException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5196421433506179782L;

    /**
     * Creates a new instance of OperationNotSupportException.
     */
    public OperationNotSupportException() {
        super();
    }

    /**
     * Creates a new instance of OperationNotSupportException.
     * 
     * @param arg0
     * @param arg1
     */
    public OperationNotSupportException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Creates a new instance of OperationNotSupportException.
     * 
     * @param arg0
     */
    public OperationNotSupportException(String arg0) {
        super(arg0);
    }

    /**
     * Creates a new instance of OperationNotSupportException.
     * 
     * @param arg0
     */
    public OperationNotSupportException(Throwable arg0) {
        super(arg0);
    }

}
