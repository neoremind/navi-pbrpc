package com.baidu.beidou.navi.pbrpc.error;

import com.baidu.beidou.navi.pbrpc.protocol.NsHead;

/**
 * ClassName: ErrorCode <br/>
 * Function: 常见错误信息枚举，用于填充{@link NsHead}内的<tt>flags</tt>usigned short(4)，用于服务端和客户端的错误信息交换。<br/>
 * 这里注意一般情况希望错误返回信息包括errCode、returnCode等由业务逻辑维护，这里是针对Pbprc这个框架做的异常，如果业务逻辑没有处理自己的异常，那么实际客户端是没有办法知道详细信息的，只会有一个笼统的未知异常告知客户端
 * 
 * @author Zhang Xu
 */
public enum ErrorCode {

    SERVICE_NOT_FOUND(0x7f, "Service not found "), PROTOBUF_CODEC_ERROR(0x7e,
            "Protobuf codec failed "), INVOCATION_TARGET_EXCEPTION(0x7d,
            "Invocation method on target bean failed "), UNEXPECTED_ERROR(0x7c,
            "Unexpected error occurred which should not happen "), COMMUNICATION_ERROR(0x7b,
            "Communication error occurred ");

    /**
     * 错误码
     */
    private int value = 0;

    /**
     * 错误消息
     */
    private String message = "";

    /**
     * Creates a new instance of ErrorCode.
     * 
     * @param value
     * @param message
     */
    private ErrorCode(int value, String message) {
        this.value = value;
        this.message = message;
    }

    /**
     * 根据错误码返回错误枚举
     * 
     * @param errorCode
     * @return
     */
    public static ErrorCode get(int errorCode) {
        if (SERVICE_NOT_FOUND.getValue() == errorCode) {
            return SERVICE_NOT_FOUND;
        } else if (PROTOBUF_CODEC_ERROR.getValue() == errorCode) {
            return PROTOBUF_CODEC_ERROR;
        } else if (INVOCATION_TARGET_EXCEPTION.getValue() == errorCode) {
            return INVOCATION_TARGET_EXCEPTION;
        } else if (UNEXPECTED_ERROR.getValue() == errorCode) {
            return UNEXPECTED_ERROR;
        } else if (COMMUNICATION_ERROR.getValue() == errorCode) {
            return COMMUNICATION_ERROR;
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
