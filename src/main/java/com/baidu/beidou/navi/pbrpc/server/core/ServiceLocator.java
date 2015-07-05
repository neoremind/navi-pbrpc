package com.baidu.beidou.navi.pbrpc.server.core;

/**
 * ClassName: ServiceLocator <br/>
 * Function: 服务定位器，用于注入服务到运行容器，同时结合netty handler路由到指定方法，另外还可以发布服务，例如到zookeeper中。
 * 
 * @author Zhang Xu
 */
public interface ServiceLocator<KEY> {

    /**
     * 根据服务标示获取服务描述
     * 
     * @param key
     * @return
     */
    ServiceDescriptor<KEY> getServiceDescriptor(KEY key);

    /**
     * 注入服务
     * 
     * @param key
     * @param serviceBean
     * @return
     */
    boolean regiserService(KEY key, Object serviceBean);

    /**
     * 整体发布服务
     * 
     * @return
     */
    boolean publishService();

}
