package com.baidu.beidou.navi.pbrpc.error;

import com.baidu.beidou.navi.pbrpc.exception.CodecException;
import com.baidu.beidou.navi.pbrpc.exception.CommunicationException;
import com.baidu.beidou.navi.pbrpc.exception.ServerExecutionException;
import com.baidu.beidou.navi.pbrpc.exception.ServiceNotFoundException;
import com.baidu.beidou.navi.pbrpc.protocol.NsHead;

/**
 * ClassName: ExceptionUtil <br/>
 * Function: 客户端构造异常工具
 * 
 * @author Zhang Xu
 */
public class ExceptionUtil {

    /**
     * 根据{@link NsHead}中保存的<tt>flags</tt>中定义的errorCode构造异常
     * 
     * @param errorCode
     * @return
     */
    public static RuntimeException buildFromErrorCode(ErrorCode errorCode) {
        if (errorCode == ErrorCode.PROTOBUF_CODEC_ERROR) {
            return new CodecException(
                    "Serialization failed at server, please check proto compatiblity");
        } else if (errorCode == ErrorCode.SERVICE_NOT_FOUND) {
            return new ServiceNotFoundException(
                    "Service not found, please check serviceId specified");
        } else if (errorCode == ErrorCode.INVOCATION_TARGET_EXCEPTION
                || errorCode == ErrorCode.UNEXPECTED_ERROR) {
            return new ServerExecutionException(
                    "Exception occurred at server, and cause can be only get from server");
        } else if (errorCode == ErrorCode.COMMUNICATION_ERROR) {
            return new CommunicationException();
        } else {
            return new RuntimeException(
                    "Failed to specify server error though there is error happened");
        }
    }

}
