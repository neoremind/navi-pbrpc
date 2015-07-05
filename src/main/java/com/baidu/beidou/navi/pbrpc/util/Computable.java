package com.baidu.beidou.navi.pbrpc.util;

import java.util.concurrent.Callable;

/**
 * ClassName: Computable <br/>
 * Function: 计算类型任务的接口
 * 
 * @author Xu Chen
 */
public interface Computable<K, V> {

    /**
     * 通过关键字来计算
     * 
     * @param key
     *            查找关键字
     * @param callable
     *            # @see Callable
     * @return 计算结果
     */
    V get(K key, Callable<V> callable);

}
