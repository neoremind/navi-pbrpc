package com.baidu.beidou.navi.pbrpc.it;

import org.junit.Test;

import com.baidu.beidou.navi.pbrpc.client.HAPbrpcClientFactory;
import com.baidu.beidou.navi.pbrpc.client.PbrpcClient;
import com.baidu.beidou.navi.pbrpc.client.ha.FailFastStrategy;
import com.baidu.beidou.navi.pbrpc.client.ha.FailOverStrategy;
import com.baidu.beidou.navi.pbrpc.client.ha.RRLoadBalanceStrategy;
import com.baidu.beidou.navi.pbrpc.client.ha.RandomLoadBalanceStrategy;
import com.baidu.beidou.navi.pbrpc.exception.client.PbrpcConnectionException;
import com.baidu.beidou.navi.pbrpc.exception.client.PbrpcException;

public class HAPbrpcClient4BlockingIOTest extends BaseTest {

    private static final String UNCONNT_IPPORT = "9.9.9.9:9999";

    @Test
    public void testSyncCallWithShortAliveBlockingIOConnect() throws Exception {
        syncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return HAPbrpcClientFactory.buildShortLiveBlockingIOConnection(IPPORT,
                        new RRLoadBalanceStrategy(new FailFastStrategy()));
            }
        });
    }

    @Test
    public void testSyncCallWithPooledBlockingIOConnect() throws Exception {
        syncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return HAPbrpcClientFactory.buildPooledBlockingIOConnection(IPPORT,
                        new RRLoadBalanceStrategy(new FailFastStrategy()));
            }
        });
    }

    @Test
    public void testNegativeSyncCallWithShortAliveBlockingIOConnectRRFast() throws Exception {
        syncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return HAPbrpcClientFactory.buildShortLiveBlockingIOConnection(IPPORT + ","
                        + UNCONNT_IPPORT, 1000, 5000, new RRLoadBalanceStrategy(
                        new FailFastStrategy()));
            }
        }, 5, new PbrpcConnectionException(), false);
    }

    @Test
    public void testNegativeSyncCallWithShortAliveBlockingIOConnectRDFast() throws Exception {
        syncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return HAPbrpcClientFactory.buildShortLiveBlockingIOConnection(UNCONNT_IPPORT,
                        1000, 5000, new RandomLoadBalanceStrategy(new FailFastStrategy()));
            }
        }, new PbrpcConnectionException(), false);
    }

    @Test
    public void testNegativeSyncCallWithShortAliveBlockingIOConnectRROver() throws Exception {
        syncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return HAPbrpcClientFactory.buildShortLiveBlockingIOConnection(IPPORT + ","
                        + UNCONNT_IPPORT, 1000, 5000, new RRLoadBalanceStrategy(
                        new FailOverStrategy(2)));
            }
        });
    }

    @Test
    public void testNegativeSyncCallWithShortAliveBlockingIOConnectRDOver() throws Exception {
        syncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return HAPbrpcClientFactory.buildShortLiveBlockingIOConnection(UNCONNT_IPPORT,
                        1000, 5000, new RandomLoadBalanceStrategy(new FailOverStrategy(2)));
            }
        }, new PbrpcConnectionException(), false);
    }

    @Test
    public void testNegativeSyncCallWithPooledBlockingIOConnectRRFast() throws Exception {
        syncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return HAPbrpcClientFactory.buildPooledBlockingIOConnection(IPPORT + ","
                        + UNCONNT_IPPORT, 1000, 5000, new RRLoadBalanceStrategy(
                        new FailFastStrategy()));
            }
        }, 5, new PbrpcException(), false);
    }

    @Test
    public void testNegativeSyncCallWithPooledBlockingIOConnectRDFast() throws Exception {
        syncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return HAPbrpcClientFactory.buildPooledBlockingIOConnection(UNCONNT_IPPORT, 1000,
                        5000, new RandomLoadBalanceStrategy(new FailFastStrategy()));
            }
        }, 5, new PbrpcException(), false);
    }

    @Test
    public void testNegativeSyncCallWithPooledBlockingIOConnectRROver() throws Exception {
        syncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return HAPbrpcClientFactory.buildPooledBlockingIOConnection(IPPORT + ","
                        + UNCONNT_IPPORT, 1000, 5000, new RRLoadBalanceStrategy(
                        new FailOverStrategy(2)));
            }
        });
    }

    @Test
    public void testNegativeSyncCallWithPooledBlockingIOConnectRDOver() throws Exception {
        syncCall(new ClientBuilder() {
            @Override
            public PbrpcClient getClient() {
                return HAPbrpcClientFactory.buildPooledBlockingIOConnection(UNCONNT_IPPORT, 1000,
                        5000, new RandomLoadBalanceStrategy(new FailOverStrategy(2)));
            }
        }, new PbrpcException(), false);
    }

}
