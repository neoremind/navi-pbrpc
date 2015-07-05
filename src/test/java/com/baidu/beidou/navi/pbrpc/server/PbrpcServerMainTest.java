package com.baidu.beidou.navi.pbrpc.server;

import com.baidu.beidou.navi.pbrpc.demo.service.impl.DemoServiceImpl;


public class PbrpcServerMainTest {
    
    public static void main(String[] args) {
        PbrpcServer server = new PbrpcServer(8088);
        server.register(100, new DemoServiceImpl());
        server.start();
    }
    
}
