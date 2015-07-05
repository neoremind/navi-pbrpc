package com.baidu.beidou.navi.pbrpc.server.core;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.util.Preconditions;

/**
 * ClassName: ServiceRegistry <br/>
 * Function: 服务注册对象，常驻内存的缓存，使用单例访问，缓存了服务的描述 <br/>
 * 泛型<tt>KEY</tt>为服务的唯一标示
 * 
 * @author Zhang Xu
 */
@SuppressWarnings("rawtypes")
public class ServiceRegistry<KEY> {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistry.class);

    /**
     * 服务注册的字典 <br/>
     * 键为服务标示<code>KEY</code>，值为服务具体描述
     * 
     * @see ServiceDescriptor
     */
    private Map<KEY, ServiceDescriptor<KEY>> serviceDescriptors;

    /**
     * 注册单例引用
     */
    private static ServiceRegistry instance;

    /**
     * 获取服务注册对象
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <E> ServiceRegistry<E> getInstance() {
        if (instance == null) {
            synchronized (ServiceRegistry.class) {
                if (instance == null) {
                    instance = new ServiceRegistry<E>();
                }
            }
        }
        return instance;
    }

    /**
     * Creates a new instance of ServiceRegistry.
     */
    private ServiceRegistry() {
        serviceDescriptors = new HashMap<KEY, ServiceDescriptor<KEY>>();
    }

    /**
     * 根据标示<code>KEY</code>获取服务描述
     * 
     * @param key
     *            服务的唯一标示
     * @return 服务描述
     * @throws IllegalStateException
     */
    public ServiceDescriptor<KEY> getServiceDescriptorByKey(KEY key) throws IllegalStateException {
        Preconditions.checkNotNull(key, "Key cannot be null");
        Preconditions.checkState(instance != null, "ServiceRegistry not init yet");
        return serviceDescriptors.get(key);
    }

    /**
     * 加入服务描述
     * 
     * @param key
     *            服务的唯一标示
     * @param serviceDescriptor
     *            服务描述
     */
    public void addServiceDescriptor(KEY key, ServiceDescriptor<KEY> serviceDescriptor) {
        Preconditions.checkNotNull(key, "Key cannot be null");
        Preconditions.checkState(instance != null, "ServiceRegistry not init yet");
        if (serviceDescriptors.containsKey(key)) {
            LOG.warn("Key=" + key + " will be override with " + serviceDescriptor);
        }
        this.serviceDescriptors.put(key, serviceDescriptor);
    }

}
