package com.baidu.beidou.navi.pbrpc.server.core.impl;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.server.core.MethodResolver;
import com.baidu.beidou.navi.pbrpc.util.ReflectionUtil;
import com.google.protobuf.GeneratedMessage;

/**
 * ClassName: SimpleMethodResolver <br/>
 * Function: 默认的简单服务暴露判断类
 * 
 * @author Zhang Xu
 */
public class SimpleMethodResolver implements MethodResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleMethodResolver.class);

    /**
     * 判断某个方法是否可以暴露为服务，这里的判断条件是满足以下
     * <ul>
     * <li>1)参数只有一个</li>
     * <li>2)参数必须是protoc自动生成的GeneratedMessage类型的子类</li>
     * <li>3)返回不能为void</li>
     * <li>4)返回必须是protoc自动生成的GeneratedMessage类型的子类</li>
     * </ul>
     * 
     * @see com.baidu.beidou.navi.pbrpc.server.core.MethodResolver#isSupport(java.lang.reflect.Method)
     */
    @Override
    public boolean isSupport(Method m) {
        Class<?>[] paramTypes = m.getParameterTypes();
        Class<?> returnType = m.getReturnType();
        if (paramTypes.length != 1) {
            LOG.warn("Pbrpc only supports one parameter, skip " + m.getName());
            return false;
        }
        if (paramTypes[0].isAssignableFrom(GeneratedMessage.class)) {
            LOG.warn("Method argument type is not GeneratedMessage, skip " + m.getName());
            return false;
        }
        if (ReflectionUtil.isVoid(returnType)) {
            LOG.warn("Method return type should not be void, skip " + m.getName());
            return false;
        }
        if (returnType.isAssignableFrom(GeneratedMessage.class)) {
            LOG.warn("Method return type is not GeneratedMessage, skip " + m.getName());
            return false;
        }

        return true;
    }

}
