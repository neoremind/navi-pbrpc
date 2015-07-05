package com.baidu.beidou.navi.pbrpc.server;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.baidu.beidou.navi.pbrpc.server.core.ServiceLocator;
import com.baidu.beidou.navi.pbrpc.server.core.impl.IdKeyServiceLocator;

public class ServiceLocatorTest {

    @Test
    public void testNegative() {
        ServiceLocator<Integer> sl = new IdKeyServiceLocator();
        sl.regiserService(0, new NegativeServiceImpl());
        assertThat(sl.getServiceDescriptor(0), nullValue());
    }

}
