package com.baidu.beidou.navi.pbrpc.server;

import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoRequest;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoResponse;

public class NegativeServiceImpl {

    public Integer m1(Object arg1, Integer arg2) {
        return null;
    }

    public DemoResponse m2(Object arg1) {
        return null;
    }

    public void m3(DemoRequest arg1) {
        return;
    }

    public DemoResponse m4(DemoRequest arg1, Object arg2) {
        return null;
    }

}
