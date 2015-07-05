package com.baidu.beidou.navi.pbrpc.client;

import com.baidu.beidou.navi.pbrpc.client.callback.CallFuture;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoRequest;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoResponse;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;

public class ShortLiveConnectionPbrpcClientMainTest {

    public static void main(String[] args) throws Exception {
        PbrpcClient client = PbrpcClientFactory.buildPooledConnection("127.0.0.1", 8088, 60000);

        PbrpcMsg msg = new PbrpcMsg();
        msg.setServiceId(100);
        msg.setProvider("beidou");
//
//        for (int i = 0; i < 10000; i++) {
//            Thread.sleep(5000);
//            System.out.println("got here");
//            // async
//            long start = System.currentTimeMillis();
//            msg.setData(getData(1));
//            try {
//                CallFuture<DemoResponse> future = client.asyncTransport(DemoResponse.class, msg);
//                DemoResponse res = future.get();
//                System.out.println("----\n" + res);
//                System.out.println("Invoke using " + (System.currentTimeMillis() - start) + "ms");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        // async
        long start = System.currentTimeMillis();
        msg.setData(getData(1));
        CallFuture<DemoResponse> future = client.asyncTransport(DemoResponse.class, msg);
        DemoResponse res = future.get();
        System.out.println("----\n" + res);
        System.out.println("Invoke using " + (System.currentTimeMillis() - start) + "ms");

        // sync
        start = System.currentTimeMillis();
        msg.setData(getData(5));
        res = client.syncTransport(DemoResponse.class, msg);
        System.out.println("----\n" + res);
        System.out.println("Invoke using " + (System.currentTimeMillis() - start) + "ms");

    }

    private static byte[] getData(int userId) {
        DemoRequest.Builder req = DemoRequest.newBuilder();
        req.setUserId(userId);
        byte[] data = req.build().toByteArray();
        return data;
    }

}
