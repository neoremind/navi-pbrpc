package com.baidu.beidou.navi.pbrpc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一般标识在客户端的接口类上，便于在{@link com.baidu.beidou.navi.pbrpc.spring.IntegrationProxy}实现类内部做{@link com.baidu.beidou.navi
 * .pbrpc.transport.PbrpcMsg}中的<code>serviceId</code>填充
 *
 * @author zhangxu
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PbrpcMethodId {

    /**
     * Pbprc header头中的serviceId，a.k.a methodId
     *
     * @return methodId
     */
    int value() default 0;

}
