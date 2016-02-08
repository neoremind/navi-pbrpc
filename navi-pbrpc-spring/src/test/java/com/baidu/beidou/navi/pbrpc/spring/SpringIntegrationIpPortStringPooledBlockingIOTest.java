package com.baidu.beidou.navi.pbrpc.spring;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.baidu.beidou.navi.pbrpc.client.PbrpcClient;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo;
import com.baidu.beidou.navi.pbrpc.demo.service.DemoService;
import com.baidu.beidou.navi.pbrpc.demo.service.impl.DemoServiceImpl;
import com.baidu.beidou.navi.pbrpc.server.PbrpcServer;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;

/**
 * @author zhangxu
 */
@ContextConfiguration(locations = {"classpath*:ipportstring_pooled/applicationContext.xml"})
public class SpringIntegrationIpPortStringPooledBlockingIOTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private DemoService demoService;

    @Test
    public void testDoSmth() throws Exception {
        Demo.DemoRequest.Builder req = Demo.DemoRequest.newBuilder();
        req.setUserId(1);

        int multiSize = 12;
        int totalRequestSize = 10;
        ExecutorService pool = Executors.newFixedThreadPool(multiSize);
        CompletionService<Demo.DemoResponse> completionService = new ExecutorCompletionService<Demo.DemoResponse>(
                pool);

        Invoker invoker = new Invoker(req.build());
        long time = System.currentTimeMillis();
        for (int i = 0; i < totalRequestSize; i++) {
            completionService.submit(invoker);
        }

        for (int i = 0; i < totalRequestSize; i++) {
            completionService.take().get();
        }

        long timetook = System.currentTimeMillis() - time;
        System.out.println("Total using " + timetook + "ms");
        System.out.println("QPS:" + 1000f / ((timetook) / (1.0f * totalRequestSize)));
    }

    private class Invoker implements Callable<Demo.DemoResponse> {

        private Demo.DemoRequest request;

        public Invoker(Demo.DemoRequest request) {
            this.request = request;
        }

        @Override
        public Demo.DemoResponse call() throws Exception {
            Demo.DemoResponse response = demoService.doSmth(request);
            System.out.println(response);
            assertThat(response.getUserId(), is(1));
            return response;
        }

    }

    private static byte[] getData(int userId) {
        Demo.DemoRequest.Builder req = Demo.DemoRequest.newBuilder();
        req.setUserId(userId);
        byte[] data = req.build().toByteArray();
        return data;
    }

    protected static PbrpcServer SERVER = null;
    protected static PbrpcServer SERVER_HA = null;

    protected static int PORT = 14419;
    protected static int PORT_HA = 14420;

    @BeforeClass
    public static void setUp() {
        System.out.println("Start server now...");
        SERVER = new PbrpcServer(PORT);
        SERVER.register(100, new DemoServiceImpl());
        SERVER.start();

        SERVER_HA = new PbrpcServer(PORT_HA);
        SERVER_HA.register(100, new DemoServiceImpl());
        SERVER_HA.start();
    }

    @AfterClass
    public static void tearDown() {
        if (SERVER != null) {
            SERVER.shutdown();
        }
        if (SERVER_HA != null) {
            SERVER_HA.shutdown();
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
