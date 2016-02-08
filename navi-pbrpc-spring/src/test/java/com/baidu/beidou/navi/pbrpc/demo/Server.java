package com.baidu.beidou.navi.pbrpc.demo;

import com.baidu.beidou.navi.pbrpc.demo.service.impl.DemoServiceImpl;
import com.baidu.beidou.navi.pbrpc.server.PbrpcServer;

/**
 * ClassName: Server <br/>
 * Function: 测试用服务端
 * 
 * @author Zhang Xu
 */
public class Server {

    /**
     * run
     * 
     * @param port
     */
    public void run(int port) {
        PbrpcServer server = new PbrpcServer(port);
        server.register(100, new DemoServiceImpl());
        server.start();
    }

}
