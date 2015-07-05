package com.baidu.beidou.navi.pbrpc.it;

import io.netty.channel.ConnectTimeoutException;

import org.junit.Test;

import com.baidu.beidou.navi.pbrpc.client.PbrpcClient;
import com.baidu.beidou.navi.pbrpc.client.PbrpcClientFactory;
import com.baidu.beidou.navi.pbrpc.exception.TimeoutException;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;

public class ShortAliveConnectionPbrpcClientTest extends BaseTest {

    @Test
    public void testAsyncCall() throws Exception {
        asyncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return PbrpcClientFactory.buildShortLiveConnection(IP, PORT);
            }
        });
    }

    @Test
    public void testSyncCall() throws Exception {
        syncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return PbrpcClientFactory.buildShortLiveConnection(IP, PORT, 2000, 5000);
            }
        });
    }

    @Test
    public void testSyncCallTimeout() throws Exception {
        PbrpcMsg msg = getPbrpcMsg(1).setServiceId(101);
        asyncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return PbrpcClientFactory.buildShortLiveConnection(IP, PORT, 500);
            }
        }, msg, new TimeoutException(), false);
    }

    @Test
    public void testNegativeConnectCall() throws Exception {
        asyncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return PbrpcClientFactory.buildShortLiveConnection("9.9.9.9", 9999);
            }
        }, new ConnectTimeoutException(), false);
    }

}
