package com.baidu.beidou.navi.pbrpc.spring;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.baidu.beidou.navi.pbrpc.demo.proto.Demo;
import com.baidu.beidou.navi.pbrpc.demo.service.DemoService;
import com.baidu.beidou.navi.pbrpc.demo.service.impl.DemoServiceImpl;
import com.baidu.beidou.navi.pbrpc.server.PbrpcServer;

/**
 * @author zhangxu
 */
@ContextConfiguration(locations = {"classpath*:ipportstring_nagative/applicationContext.xml"})
public class SpringIntegrationIpPortStringHATest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private DemoService demoService;

    @Test
    public void testDoSmth() {
        Demo.DemoRequest.Builder req = Demo.DemoRequest.newBuilder();
        req.setUserId(1);
        Demo.DemoResponse response = demoService.doSmth(req.build());
        System.out.println(response);
        assertThat(response.getUserId(), is(1));
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
