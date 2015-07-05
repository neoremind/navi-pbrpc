package com.baidu.beidou.navi.pbrpc.it;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.baidu.beidou.navi.pbrpc.client.PbrpcClientFactory;
import com.baidu.beidou.navi.pbrpc.client.callback.CallFuture;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoBatchRequest;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoBatchResponse;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;

public class ComplicatedResponsePbrpcClientTest extends BaseTest {

    @Test
    public void testComplicatedCall() throws Exception {
        System.out.println("============================");
        System.out.println("async call test starts ...");
        System.out.println("============================");

        CLIENT = PbrpcClientFactory.buildShortLiveConnection(IP, PORT);
        System.out.println(CLIENT.getInfo());

        // async
        for (int i = 1; i <= 2; i++) {
            System.out.println("call-" + i + " starts...");
            long start = System.currentTimeMillis();
            DemoBatchResponse res = null;
            CallFuture<DemoBatchResponse> f = CLIENT.asyncTransport(DemoBatchResponse.class,
                    getPbrpcMsg());
            res = f.get();
            System.out.println("----\n" + res);
            assertThat(res.getTextsList().size(), is(5));
            assertThat(res.getTextsList().get(0).getText(), is("abcdefg"));
            System.out.println("Call-" + i + " ends using " + (System.currentTimeMillis() - start)
                    + "ms");
        }
    }

    public PbrpcMsg getPbrpcMsg() {
        PbrpcMsg msg = new PbrpcMsg();
        msg.setServiceId(102);
        msg.setProvider("beidou");
        msg.setData(getData(5, "abcdefg"));
        return msg;
    }

    public byte[] getData(int size, String text) {
        DemoBatchRequest.Builder req = DemoBatchRequest.newBuilder();
        req.setRequestSize(size);
        req.setText(text);
        byte[] data = req.build().toByteArray();
        return data;
    }

}
