package com.baidu.beidou.navi.pbrpc.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.annotation.PbrpcMethodId;
import com.baidu.beidou.navi.pbrpc.client.PbrpcClient;
import com.baidu.beidou.navi.pbrpc.exception.client.PbrpcException;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;
import com.google.protobuf.GeneratedMessage;

/**
 * JDK动态代理生成代理实例对象
 *
 * @author zhangxu
 */
public class JdkDynamicIntegrationProxy implements IntegrationProxy {

    private static final Logger LOG = LoggerFactory.getLogger(JdkDynamicIntegrationProxy.class);

    /**
     * Pbrpc客户端
     */
    private PbrpcClient pbrpcClient;

    /**
     * {@link PbrpcMsg}中的<code>provider</code>，用于Pbrpc请求头，标示调用者来源，例如填充团队名称
     */
    private String provider;

    /**
     * 创建指定接口的实例对象
     *
     * @param clazz 类型
     *
     * @return 代理实例对象
     */
    @Override
    public <T> T createProxy(Class<T> clazz) {
        return (T) (Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[] {clazz}, new JdkDynamicProxyHandler()));
    }

    /**
     * JDK动态代理的InvocationHandler，AOP执行期间拦截接口调用的实现
     *
     * @author zhangxu
     */
    class JdkDynamicProxyHandler implements InvocationHandler {

        /**
         * 代理运行时拦截的方法
         *
         * @param proxy  代理接口
         * @param method 代理方法
         * @param args   参数列表
         *
         * @return 代理实例对象
         *
         * @throws Throwable
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format(
                        "Proxy bean starts to invoke pbrpc on %s %s.%s(%s)",
                        method.getReturnType().getSimpleName(),
                        method.getDeclaringClass().getSimpleName(),
                        method.getName(),
                        Arrays.toString(args)));
            }

            // check arguments and returnType
            if (args == null || args.length != 1) {
                throw new PbrpcException("Pbrpc only support one argument");
            }
            if (!GeneratedMessage.class.isAssignableFrom(method.getParameterTypes()[0])) {
                throw new PbrpcException("Pbrpc method argument should be an instance of GeneratedMessage");
            }
            if (!GeneratedMessage.class.isAssignableFrom(method.getReturnType())) {
                throw new PbrpcException("Pbrpc method returnType should be an instance of GeneratedMessage");
            }

            Class<? extends GeneratedMessage> responseClass =
                    (Class<? extends GeneratedMessage>) method.getReturnType();

            PbrpcMsg msg = new PbrpcMsg();
            msg.setServiceId(getMethodId(method));
            msg.setProvider(provider);
            msg.setData(((GeneratedMessage) args[0]).toByteArray());
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("provider=%s, methodId=%d", msg.getProvider(), msg.getServiceId()));
            }
            GeneratedMessage response = pbrpcClient.syncTransport(responseClass, msg);
            return response;
        }

        /**
         * 从接口的注解中获取填充Pbrpc头的methodId，如果没有{@link PbrpcMethodId}注解，则默认为0
         *
         * @param method 方法
         *
         * @return methodId
         */
        private int getMethodId(Method method) {
            PbrpcMethodId pbrpcMethodId = method.getAnnotation(PbrpcMethodId.class);
            if (pbrpcMethodId == null) {
                return 0;
            }
            return pbrpcMethodId.value();
        }
    }

    public void setPbrpcClient(PbrpcClient pbrpcClient) {
        this.pbrpcClient = pbrpcClient;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
