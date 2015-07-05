package com.baidu.beidou.navi.pbrpc.client;

import com.baidu.beidou.navi.pbrpc.client.callback.CallFuture;
import com.baidu.beidou.navi.pbrpc.client.ha.FailOverStrategy;
import com.baidu.beidou.navi.pbrpc.client.ha.RandomLoadBalanceStrategy;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoRequest;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoResponse;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;

public class HAPbrpcClientMainTest {

    public static void main(String[] args) throws Exception {
        PbrpcClient client = HAPbrpcClientFactory.buildShortLiveConnection("127.0.0.1:8088,1.1.1.1:9999",
                new RandomLoadBalanceStrategy(new FailOverStrategy(2)));

        PbrpcMsg msg = new PbrpcMsg();
        msg.setServiceId(100);
        msg.setProvider("beidou");

        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();
            msg.setData(getData(1));
            CallFuture<DemoResponse> future = client.asyncTransport(DemoResponse.class, msg);
            DemoResponse res = future.get();
            System.out.println("----\n" + res);
            System.out.println("Invoke using " + (System.currentTimeMillis() - start) + "ms");
        }

        // async
        long start = System.currentTimeMillis();
        msg.setData(getData(1));
        CallFuture<DemoResponse> future = client.asyncTransport(DemoResponse.class, msg);
        DemoResponse res = future.get();
        System.out.println("----\n" + res);
        System.out.println("Invoke using " + (System.currentTimeMillis() - start) + "ms");
        //
        // // sync
        // start = System.currentTimeMillis();
        // msg.setData(getData(5));
        // res = client.syncTransport(DemoResponse.class, msg);
        // System.out.println("----\n" + res);
        // System.out.println("Invoke using " + (System.currentTimeMillis() - start) + "ms");

    }

    private static byte[] getData(int userId) {
        DemoRequest.Builder req = DemoRequest.newBuilder();
        req.setUserId(userId);
        byte[] data = req.build().toByteArray();
        return data;
    }

}
