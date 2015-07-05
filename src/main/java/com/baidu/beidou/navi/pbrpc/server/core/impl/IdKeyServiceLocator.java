package com.baidu.beidou.navi.pbrpc.server.core.impl;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.server.core.MethodResolver;
import com.baidu.beidou.navi.pbrpc.server.core.ServiceDescriptor;
import com.baidu.beidou.navi.pbrpc.server.core.ServiceLocator;
import com.baidu.beidou.navi.pbrpc.server.core.ServiceRegistry;
import com.baidu.beidou.navi.pbrpc.util.Preconditions;
import com.baidu.beidou.navi.pbrpc.util.ReflectionUtil;

/**
 * ClassName: IdKeyServiceLocator <br/>
 * Function: 利用methodId或者也可以叫做serviceId来做服务标示的定位器
 * 
 * @author Zhang Xu
 */
public class IdKeyServiceLocator implements ServiceLocator<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(IdKeyServiceLocator.class);

    /**
     * 服务描述的缓存，内部可以按照服务标示查找
     */
    private ServiceRegistry<Integer> serviceRegistry;

    /**
     * 判断方法是否为可暴露为服务的解析工具类
     */
    private MethodResolver methodResolver;

    /**
     * Creates a new instance of IdKeyServiceLocator.
     */
    public IdKeyServiceLocator() {
        serviceRegistry = ServiceRegistry.getInstance();
        methodResolver = new SimpleMethodResolver();
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.server.core.ServiceLocator#getServiceDescriptor(java.lang.Object)
     */
    @Override
    public ServiceDescriptor<Integer> getServiceDescriptor(Integer key) {
        Preconditions.checkNotNull(key, "Key cannot be null");
        return serviceRegistry.getServiceDescriptorByKey(key);
    }

    /**
     * 注册serviceBean，暴露接口。<br/>
     * 如果serviceBean有多个方法接口，则在key的基础上+1，依次递增方法id，也就是说这个key只是一个起始值。
     * 
     * @see com.baidu.beidou.navi.pbrpc.server.core.ServiceLocator#regiserService(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean regiserService(Integer key, Object serviceBean) {
        Method[] ms = ReflectionUtil.getAllInstanceMethods(serviceBean.getClass());
        int incr = 0;
        for (Method m : ms) {
            if (methodResolver.isSupport(m)) {
                ServiceDescriptor<Integer> desc = new ServiceDescriptor<Integer>();
                desc.setServiceId(key).setMethod(m).setTarget(serviceBean)
                        .setArgumentClass(m.getParameterTypes()[0])
                        .setReturnClass(m.getReturnType());
                int realKey = key + incr;
                serviceRegistry.addServiceDescriptor(realKey, desc);
                LOG.info(String.format("Register service key=[%d], %s %s#%s(%s) successfully",
                        realKey, m.getReturnType().getSimpleName(), serviceBean.getClass()
                                .getName(), m.getName(), m.getParameterTypes()[0].getSimpleName()));
                incr++;
            }
        }
        return false;
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.server.core.ServiceLocator#publishService()
     */
    @Override
    public boolean publishService() {
        LOG.info("Service publishing is disabled right now");
        return true;
    }

}
