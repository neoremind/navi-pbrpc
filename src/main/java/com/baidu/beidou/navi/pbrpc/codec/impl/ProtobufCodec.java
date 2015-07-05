package com.baidu.beidou.navi.pbrpc.codec.impl;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import com.baidu.beidou.navi.pbrpc.codec.Codec;
import com.baidu.beidou.navi.pbrpc.exception.CodecException;
import com.baidu.beidou.navi.pbrpc.util.Computable;
import com.baidu.beidou.navi.pbrpc.util.ConcurrentCache;
import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: ProtobufCodec <br/>
 * Function: protobuf序列化器，利用反射缓存<tt>method</tt>来进行调用
 * 
 * @author Zhang Xu
 */
public class ProtobufCodec implements Codec {

    /**
     * Protobuf生成原生Java代码中的方法解码方法名称
     */
    private static final String METHOD_NAME_PARSEFROM = "parseFrom";

    /**
     * Protobuf生成原生Java代码中的方法编码方法名称
     */
    private static final String METHOD_NAME_TOBYTE = "toByteArray";

    /**
     * 方法缓存，用于Protobuf生成原生Java代码中的某些编解码方法。 缓存的方法包括:
     * <p/>
     * <ul>
     * <li><code>parseFrom(byte[] bytes)</code></li>
     * <li><code>toByteArray()</code></li>
     * </ul>
     * 
     * @see com.baidu.beidou.navi.pbrpc.util.ConcurrentCache
     * @see com.baidu.beidou.navi.pbrpc.util.Computable
     */
    private static final Computable<String, Method> PROTOBUF_METHOD_CACHE = new ConcurrentCache<String, Method>();

    /**
     * @see com.baidu.beidou.navi.pbrpc.codec.Codec#decode(java.lang.Class, byte[])
     */
    @Override
    public Object decode(final Class<?> clazz, byte[] data) throws CodecException {
        try {
            if (data == null || data.length == 0) {
                return null;
            }
            Method m = PROTOBUF_METHOD_CACHE.get(clazz.getName() + METHOD_NAME_PARSEFROM,
                    new Callable<Method>() {
                        @Override
                        public Method call() throws Exception {
                            return clazz.getMethod(METHOD_NAME_PARSEFROM, byte[].class);
                        }
                    });
            GeneratedMessage msg = (GeneratedMessage) m.invoke(clazz, data);
            return msg;
        } catch (Exception e) {
            throw new CodecException("Decode failed due to " + e.getMessage(), e);
        }
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.codec.Codec#encode(java.lang.Class, java.lang.Object)
     */
    @Override
    public byte[] encode(final Class<?> clazz, Object object) throws CodecException {
        try {
            Method m = PROTOBUF_METHOD_CACHE.get(clazz.getName() + METHOD_NAME_TOBYTE,
                    new Callable<Method>() {
                        @Override
                        public Method call() throws Exception {
                            return clazz.getMethod(METHOD_NAME_TOBYTE);
                        }
                    });
            byte[] data = (byte[]) m.invoke(object);
            return data;
        } catch (Exception e) {
            throw new CodecException("Encode failed due to " + e.getMessage(), e);
        }
    }

}
