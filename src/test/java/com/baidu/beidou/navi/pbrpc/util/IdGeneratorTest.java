package com.baidu.beidou.navi.pbrpc.util;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class IdGeneratorTest {

    @Test
    public void testGetId() {
        int id = IdGenerator.genUUID();
        assertThat(id, greaterThan(0));
    }

}
