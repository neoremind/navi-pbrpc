package com.baidu.beidou.navi.pbrpc.it;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.baidu.beidou.navi.pbrpc.client.PbrpcClient;
import com.baidu.beidou.navi.pbrpc.client.callback.CallFuture;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoRequest;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoResponse;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoResponse.GenderType;
import com.baidu.beidou.navi.pbrpc.demo.service.impl.DemoServiceImpl;
import com.baidu.beidou.navi.pbrpc.server.PbrpcServer;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;
import com.baidu.beidou.navi.pbrpc.util.PbrpcConstants;

public abstract class BaseTest {

    protected static PbrpcServer SERVER = null;

    protected static PbrpcClient CLIENT = null;

    protected static String IP = "127.0.0.1";

    protected static int PORT = 14419;

    protected static String IPPORT = IP + ":" + PORT;

    private static final int DEFAULT_RUN_NUM = 2;

    @BeforeClass
    public static void setUp() {
        System.out.println("Start server now...");
        SERVER = new PbrpcServer(PORT);
        SERVER.register(100, new DemoServiceImpl());
        SERVER.start();

        // set timeout eviction to 1 sec
        PbrpcConstants.CLIENT_TIMEOUT_EVICTOR_CHECK_INTERVAL = 1000;
        PbrpcConstants.CLIENT_TIMEOUT_EVICTOR_DELAY_START_TIME = 1000;
    }

    protected void asyncCall(ClientBuilder builder) throws Exception {
        call(builder, DEFAULT_RUN_NUM, null, null, false, true);
    }

    protected void asyncCall(ClientBuilder builder, int runNum) throws Exception {
        call(builder, runNum, null, null, false, true);
    }

    protected void asyncCall(ClientBuilder builder, Exception expectedExp, boolean isExpRootCause)
            throws Exception {
        call(builder, 1, null, expectedExp, isExpRootCause, true);
    }

    protected void asyncCall(ClientBuilder builder, int runNum, Exception expectedExp,
            boolean isExpRootCause) throws Exception {
        call(builder, runNum, null, expectedExp, isExpRootCause, true);
    }

    protected void asyncCall(ClientBuilder builder, PbrpcMsg msg, Exception expectedExp,
            boolean isExpRootCause) throws Exception {
        call(builder, 1, msg, expectedExp, isExpRootCause, true);
    }

    protected void asyncCall(ClientBuilder builder, int runNum, PbrpcMsg msg,
            Exception expectedExp, boolean isExpRootCause) throws Exception {
        call(builder, runNum, msg, expectedExp, isExpRootCause, true);
    }

    protected void syncCall(ClientBuilder builder) throws Exception {
        call(builder, DEFAULT_RUN_NUM, null, null, false, false);
    }

    protected void syncCall(ClientBuilder builder, int runNum) throws Exception {
        call(builder, runNum, null, null, false, false);
    }

    protected void syncCall(ClientBuilder builder, Exception expectedExp, boolean isExpRootCause)
            throws Exception {
        call(builder, 1, null, expectedExp, isExpRootCause, false);
    }

    protected void syncCall(ClientBuilder builder, int runNum, Exception expectedExp,
            boolean isExpRootCause) throws Exception {
        call(builder, runNum, null, expectedExp, isExpRootCause, false);
    }

    protected void syncCall(ClientBuilder builder, PbrpcMsg msg, Exception expectedExp,
            boolean isExpRootCause) throws Exception {
        call(builder, 1, msg, expectedExp, isExpRootCause, false);
    }

    protected void syncCall(ClientBuilder builder, int runNum, PbrpcMsg msg, Exception expectedExp,
            boolean isExpRootCause) throws Exception {
        call(builder, runNum, msg, expectedExp, isExpRootCause, false);
    }

    protected void call(ClientBuilder builder, int runNum, PbrpcMsg msg, Exception expectedExp,
            boolean isExpRootCause, boolean isAsync) throws Exception {
        System.out.println("============================");
        System.out.println(isAsync ? "async call test starts ..." : "sync call test starts...");
        System.out.println("============================");

        CLIENT = builder.getClient();
        System.out.println(CLIENT.getInfo());

        // if msg is null be default call service 100 and get random user
        if (msg == null) {
            msg = getPbrpcMsg(1);
        }

        // async
        try {
            for (int i = 1; i <= runNum; i++) {
                System.out.println("call-" + i + " starts...");
                long start = System.currentTimeMillis();
                DemoResponse res = null;
                if (isAsync) {
                    CallFuture<DemoResponse> f = CLIENT.asyncTransport(DemoResponse.class, msg);
                    res = f.get();
                } else {
                    res = CLIENT.syncTransport(DemoResponse.class, msg);
                }
                System.out.println("----\n" + res);
                assertThat(res.getUserId(), is(1));
                assertThat(res.getUserName(), is("name-1"));
                assertThat(res.getGenderType(), is(GenderType.FEMALE));
                System.out.println("Call-" + i + " ends using "
                        + (System.currentTimeMillis() - start) + "ms");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (expectedExp != null) {
                if (isExpRootCause) {
                    assertThat(e.getClass().getName(), is(expectedExp.getClass().getName()));
                } else {
                    assertThat(e.getCause().getClass().getName(), is(expectedExp.getClass()
                            .getName()));
                }
            } else {
                fail("exception occurs, should not get here");
            }
            return;
        }
        if (expectedExp != null) {
            fail("should not get here");
        }
    }

    @AfterClass
    public static void tearDown() {
        if (CLIENT != null) {
            CLIENT.shutdown();
        }
        if (SERVER != null) {
            SERVER.shutdown();
        }
    }

    public PbrpcMsg getPbrpcMsg(int userId) {
        PbrpcMsg msg = new PbrpcMsg();
        msg.setServiceId(100);
        msg.setProvider("beidou");
        msg.setData(getData(userId));
        return msg;
    }

    public byte[] getData(int userId) {
        DemoRequest.Builder req = DemoRequest.newBuilder();
        req.setUserId(userId);
        byte[] data = req.build().toByteArray();
        return data;
    }

}
