package com.baidu.beidou.navi.pbrpc.server.core;

import java.lang.reflect.Method;

/**
 * ClassName: ServiceDescriptor <br/>
 * Function: 一个暴露的rpc服务的描述
 * 
 * @author Zhang Xu
 */
public class ServiceDescriptor<KEY> {

    /**
     * 服务的id，唯一标示
     */
    private KEY serviceId;

    /**
     * 服务缓存的method
     */
    private Method method;

    /**
     * 服务具体的实现bean对象
     */
    private Object target;

    /**
     * 服务输入参数对象
     */
    private Class<?> argumentClass;

    /**
     * 服务输出参数对象
     */
    private Class<?> returnClass;

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ServiceId=");
        sb.append(serviceId);
        sb.append(", ");
        sb.append(returnClass.getSimpleName());
        sb.append(" ");
        sb.append(target.getClass().getSimpleName());
        sb.append(".");
        sb.append(method.getName());
        sb.append("(");
        sb.append(argumentClass.getClass().getSimpleName());
        sb.append(")");
        return sb.toString();
    }

    public KEY getServiceId() {
        return serviceId;
    }

    public ServiceDescriptor<KEY> setServiceId(KEY serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public Method getMethod() {
        return method;
    }

    public ServiceDescriptor<KEY> setMethod(Method method) {
        this.method = method;
        return this;
    }

    public Object getTarget() {
        return target;
    }

    public ServiceDescriptor<KEY> setTarget(Object target) {
        this.target = target;
        return this;
    }

    public Class<?> getArgumentClass() {
        return argumentClass;
    }

    public ServiceDescriptor<KEY> setArgumentClass(Class<?> argumentClass) {
        this.argumentClass = argumentClass;
        return this;
    }

    public Class<?> getReturnClass() {
        return returnClass;
    }

    public ServiceDescriptor<KEY> setReturnClass(Class<?> returnClass) {
        this.returnClass = returnClass;
        return this;
    }

}
