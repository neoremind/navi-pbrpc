package com.baidu.beidou.navi.pbrpc.client;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.demo.proto.Demo;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoRequest;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoResponse;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;

public class BlockingIOPooledPbrpcClientMainTest {

    private static final Logger LOG = LoggerFactory.getLogger(PooledPbrpcClientMainTest.class);

    public static void main(String[] args) throws Exception {
        BlockingIOPooledPbrpcClientMainTest test = new BlockingIOPooledPbrpcClientMainTest();
        test.testPool();
        // test.testPoolBatch();
    }

    public void testPool() throws Exception {
        PbrpcClient client = PbrpcClientFactory.buildPooledConnection(new PooledConfiguration(),
                "127.0.0.1", 8088, 4000);

        PbrpcMsg msg;
        msg = new PbrpcMsg();
        msg.setServiceId(100);
        msg.setProvider("beidou");
        msg.setData(getData(1));
        DemoResponse res = client.asyncTransport(DemoResponse.class, msg).get();
        System.out.println(res);

        int multiSize = 12;
        int totalRequestSize = 100000;
        ExecutorService pool = Executors.newFixedThreadPool(multiSize);
        CompletionService<DemoResponse> completionService = new ExecutorCompletionService<DemoResponse>(
                pool);

        Invoker invoker = new Invoker(client);
        long time = System.currentTimeMillis();
        for (int i = 0; i < totalRequestSize; i++) {
            completionService.submit(invoker);
        }

        for (int i = 0; i < totalRequestSize; i++) {
            completionService.take().get();
        }

        long timetook = System.currentTimeMillis() - time;
        LOG.info("Total using " + timetook + "ms");
        LOG.info("QPS:" + 1000f / ((timetook) / (1.0f * totalRequestSize)));
    }

    private static byte[] getData(int userId) {
        DemoRequest.Builder req = DemoRequest.newBuilder();
        req.setUserId(userId);
        byte[] data = req.build().toByteArray();
        return data;
    }

    private class Invoker implements Callable<DemoResponse> {

        private PbrpcClient client;

        public Invoker(PbrpcClient client) {
            this.client = client;
        }

        @Override
        public DemoResponse call() throws Exception {
            PbrpcMsg msg;
            msg = new PbrpcMsg();
            msg.setServiceId(100);
            msg.setProvider("beidou");
            msg.setData(getData(1));
            DemoResponse res = client.asyncTransport(DemoResponse.class, msg).get();
            return res;
        }

    }

}
