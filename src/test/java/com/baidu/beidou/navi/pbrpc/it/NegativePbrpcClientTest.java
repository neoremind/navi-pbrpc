package com.baidu.beidou.navi.pbrpc.it;

import org.junit.Test;

import com.baidu.beidou.navi.pbrpc.client.PbrpcClient;
import com.baidu.beidou.navi.pbrpc.client.PbrpcClientFactory;
import com.baidu.beidou.navi.pbrpc.exception.CodecException;
import com.baidu.beidou.navi.pbrpc.exception.ServerExecutionException;
import com.baidu.beidou.navi.pbrpc.exception.ServiceNotFoundException;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;

public class NegativePbrpcClientTest extends BaseTest {

    @Test
    public void testMethodNotFoundException() throws Exception {
        PbrpcMsg msg = getPbrpcMsg(1).setServiceId(-1);
        asyncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return PbrpcClientFactory.buildPooledConnection(IP, PORT);
            }
        }, msg, new ServiceNotFoundException(), false);
    }

    @Test
    public void testServerExecutionException() throws Exception {
        PbrpcMsg msg = getPbrpcMsg(9999);
        asyncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return PbrpcClientFactory.buildPooledConnection(IP, PORT);
            }
        }, msg, new ServerExecutionException(), false);
    }

    @Test
    public void testCodecException() throws Exception {
        PbrpcMsg msg = getPbrpcMsg(1).setData(new String("xyz").getBytes());
        asyncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return PbrpcClientFactory.buildPooledConnection(IP, PORT);
            }
        }, msg, new CodecException(), false);
    }

}