package com.baidu.beidou.navi.pbrpc.codec;

import com.baidu.beidou.navi.pbrpc.exception.CodecException;

/**
 * ClassName: Codec <br/>
 * Function: 编码接口
 * 
 * @author Zhang Xu
 */
public interface Codec {

    /**
     * 反序列化
     * 
     * @param clazz
     *            反序列化后的类定义
     * @param bytes
     *            字节码
     * @return 反序列化后的对象
     * @throws CodecException
     */
    Object decode(Class<?> clazz, byte[] bytes) throws CodecException;

    /**
     * 序列化
     * 
     * @param clazz
     *            待序列化的类定义
     * @param object
     *            待序列化的对象
     * @return 字节码
     * @throws CodecException
     */
    byte[] encode(Class<?> clazz, Object object) throws CodecException;

}
