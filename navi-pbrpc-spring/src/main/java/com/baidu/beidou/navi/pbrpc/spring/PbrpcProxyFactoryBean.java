package com.baidu.beidou.navi.pbrpc.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 和Spring集成起来使用的动态代理工厂bean，用于生成Pbrpc接口的代理实例对象
 *
 * @author zhangxu
 */
public class PbrpcProxyFactoryBean implements FactoryBean, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(PbrpcProxyFactoryBean.class);

    /**
     * 代理实例对象生成器
     */
    private IntegrationProxy integrationProxy;

    /**
     * 调用接口
     */
    private Class<?> serviceInterface;

    @Override
    public Object getObject() throws Exception {
        return integrationProxy.createProxy(serviceInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("Create integration rpc proxy bean " + getClass().getSimpleName() + " for interface "
                + serviceInterface);
    }

    public void setIntegrationProxy(IntegrationProxy integrationProxy) {
        this.integrationProxy = integrationProxy;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }
}
