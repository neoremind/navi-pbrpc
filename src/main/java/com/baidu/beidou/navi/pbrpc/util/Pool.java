package com.baidu.beidou.navi.pbrpc.util;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.baidu.beidou.navi.pbrpc.exception.client.PbrpcConnectionException;
import com.baidu.beidou.navi.pbrpc.exception.client.PbrpcException;

/**
 * ClassName: Pool <br/>
 * Function: 封装commons-pool的抽象对象池
 * 
 * @author Zhang Xu
 */
public abstract class Pool<T> {

    /**
     * 对象池
     */
    private final GenericObjectPool internalPool;

    /**
     * Creates a new instance of Pool.
     * 
     * @param poolConfig
     * @param factory
     */
    public Pool(final GenericObjectPool.Config poolConfig, PoolableObjectFactory factory) {
        this.internalPool = new GenericObjectPool(factory, poolConfig);
    }

    /**
     * 从对象池获取一个可用对象
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public T getResource() {
        try {
            return (T) internalPool.borrowObject();
        } catch (Exception e) {
            throw new PbrpcConnectionException("Could not get a resource from the pool", e);
        }
    }

    /**
     * 返回对象到池中
     * 
     * @param resource
     */
    public void returnResourceObject(final Object resource) {
        try {
            internalPool.returnObject(resource);
        } catch (Exception e) {
            throw new PbrpcException("Could not return the resource to the pool", e);
        }
    }

    /**
     * 返回一个调用失败的对象到池中
     * 
     * @param resource
     */
    public void returnBrokenResource(final T resource) {
        returnBrokenResourceObject(resource);
    }

    /**
     * 返回对象到池中
     * 
     * @param resource
     */
    public void returnResource(final T resource) {
        returnResourceObject(resource);
    }

    /**
     * 返回一个调用失败的对象到池中
     * 
     * @param resource
     */
    protected void returnBrokenResourceObject(final Object resource) {
        try {
            internalPool.invalidateObject(resource);
        } catch (Exception e) {
            throw new PbrpcException("Could not return the resource to the pool", e);
        }
    }

    /**
     * 获取活跃的池中对象数量
     * 
     * @return
     */
    public int getNumActive() {
        if (this.internalPool == null || this.internalPool.isClosed()) {
            return -1;
        }

        return this.internalPool.getNumActive();
    }

    /**
     * 获取暂时idle的对象数量
     * 
     * @return
     */
    public int getNumIdle() {
        if (this.internalPool == null || this.internalPool.isClosed()) {
            return -1;
        }
        return internalPool.getNumIdle();
    }

    /**
     * 销毁对象池
     */
    public void destroy() {
        try {
            internalPool.close();
        } catch (Exception e) {
            throw new PbrpcException("Could not destroy the pool", e);
        }
    }

}
