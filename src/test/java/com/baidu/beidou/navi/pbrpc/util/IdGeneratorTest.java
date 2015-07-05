package com.baidu.beidou.navi.pbrpc.util;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class IdGeneratorTest {

    public void testGetId() {
        int id = IdGenerator.genUUID();
        assertThat(id, greaterThan(0));
    }

}
