package com.baidu.beidou.navi.pbrpc.spring;

/**
 * 配合Spring AOP中的{@link org.springframework.beans.factory.FactoryBean}来生成代理对象
 * <p/>
 * 该接口是一个通用的代理生成类，根据指定的对象类型，返回代理对象。对象的生成可以使用传统的JDK动态代理，也可以使用Javassist字节码增强技术
 *
 * @author zhangxu
 */
public interface IntegrationProxy {

    /**
     * 创建指定接口的实例对象
     *
     * @param clazz 类型
     *
     * @return 代理实例对象
     */
    <T> T createProxy(Class<T> clazz);

}
