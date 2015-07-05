package com.baidu.beidou.navi.pbrpc.codec.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.baidu.beidou.navi.pbrpc.codec.Codec;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoResponse;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoResponse.GenderType;

public class ProtobufCodecTest {

    private Codec codec = new ProtobufCodec();

    @Test
    public void testEncodeDecode() throws Exception {
        long start = System.currentTimeMillis();
        // codec = new ProtobufCodec();
        DemoResponse.Builder builder = DemoResponse.newBuilder();
        builder.setUserId(100);
        builder.setUserName("neoRemind");
        builder.setGenderType(GenderType.MALE);
        DemoResponse res = builder.build();
        System.out.println("origin:\n" + res);
        byte[] data = codec.encode(DemoResponse.class, res);
        System.out.println("encode using " + (System.currentTimeMillis() - start) + "ms");
        start = System.currentTimeMillis();
        // codec = new ProtobufCodec();
        DemoResponse res2 = (DemoResponse) codec.decode(DemoResponse.class, data);
        System.out.println("decode using " + (System.currentTimeMillis() - start) + "ms");
        System.out.println("after:\n" + res2);
        assertThat(res2, notNullValue());
        assertThat(res2.getUserId(), is(100));
        assertThat(res2.getUserName(), is("neoRemind"));
        assertThat(res2.getGenderType(), is(GenderType.MALE));
    }

}
