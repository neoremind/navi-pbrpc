package com.baidu.beidou.navi.pbrpc.exception;

/**
 * ClassName: CodecException <br/>
 * Function: 编解码异常
 * 
 * @author Zhang Xu
 */
public class CodecException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5196421433506179782L;

    /**
     * Creates a new instance of CodecException.
     */
    public CodecException() {
        super();
    }

    /**
     * Creates a new instance of CodecException.
     * 
     * @param arg0
     * @param arg1
     */
    public CodecException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Creates a new instance of CodecException.
     * 
     * @param arg0
     */
    public CodecException(String arg0) {
        super(arg0);
    }

    /**
     * Creates a new instance of CodecException.
     * 
     * @param arg0
     */
    public CodecException(Throwable arg0) {
        super(arg0);
    }

}
