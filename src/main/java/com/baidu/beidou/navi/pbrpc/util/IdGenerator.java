package com.baidu.beidou.navi.pbrpc.util;

import java.util.UUID;

/**
 * ClassName: IdGenerator <br/>
 * Function: id生成器
 * 
 * @author Zhang Xu
 */
public class IdGenerator {

    /**
     * 生成一个Unique ID， 默认使用JDK自带的UUID
     * <p>
     * 也可以使用：
     * 
     * <pre>
     * return UUIDGenerator.getInstance().generateTimeBasedUUID().toString();
     * return Math.abs(new Random(System.currentTimeMillis()).nextInt());
     * </pre>
     * 
     * @return
     */
    public static int genUUID() {
        return Math.abs(UUID.randomUUID().toString().hashCode());
    }

}
