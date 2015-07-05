package com.baidu.beidou.navi.pbrpc.server.core;

import java.lang.reflect.Method;

/**
 * ClassName: MethodResolver <br/>
 * Function: 判断方法是否为可暴露服务的接口
 * 
 * @author Zhang Xu
 */
public interface MethodResolver {

    /**
     * 方法是否可以暴露为服务
     * 
     * @param m
     *            方法
     * @return 是否可以暴露为pbrpc服务，true为可以，false为不行
     */
    boolean isSupport(Method m);

}
