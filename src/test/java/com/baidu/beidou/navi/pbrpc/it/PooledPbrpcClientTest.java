package com.baidu.beidou.navi.pbrpc.it;

import org.junit.Test;

import com.baidu.beidou.navi.pbrpc.client.PbrpcClient;
import com.baidu.beidou.navi.pbrpc.client.PbrpcClientFactory;
import com.baidu.beidou.navi.pbrpc.client.PooledConfiguration;
import com.baidu.beidou.navi.pbrpc.exception.TimeoutException;
import com.baidu.beidou.navi.pbrpc.exception.client.PbrpcConnectionException;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;

public class PooledPbrpcClientTest extends BaseTest {

    @Test
    public void testAsyncCall() throws Exception {
        asyncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return PbrpcClientFactory.buildPooledConnection(IP, PORT);
            }
        });
    }

    @Test
    public void testSyncCall() throws Exception {
        syncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return PbrpcClientFactory.buildPooledConnection(new PooledConfiguration(), IP,
                        PORT, 2000, 10000);
            }
        });
    }

    @Test
    public void testSyncCallTimeout() throws Exception {
        PbrpcMsg msg = getPbrpcMsg(1).setServiceId(101);
        syncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return PbrpcClientFactory.buildPooledConnection(IP, PORT, 500);
            }
        }, msg, new TimeoutException(), false);
    }

    @Test
    public void testNegativeConnectCall() throws Exception {
        asyncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return PbrpcClientFactory.buildPooledConnection("9.9.9.9", 9999);
            }
        }, new PbrpcConnectionException(), true);
    }

}
