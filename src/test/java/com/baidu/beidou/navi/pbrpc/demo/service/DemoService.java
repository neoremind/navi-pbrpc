package com.baidu.beidou.navi.pbrpc.demo.service;

import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoBatchRequest;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoBatchResponse;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoRequest;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoResponse;

/**
 * ClassName: DemoService <br/>
 * Function: demo服务端接口
 * 
 * @author Zhang Xu
 */
public interface DemoService {

    /**
     * 干点什么
     * 
     * @param req
     *            请求
     * @return 响应
     */
    DemoResponse doSmth(DemoRequest req);

    /**
     * 用于测试批量干点什么
     * 
     * @param req
     *            请求
     * @return 响应
     */
    DemoBatchResponse doSmthBatch(DemoBatchRequest req);

    /**
     * 干点什么，内部sleep一定时间，模拟超时
     * 
     * @param req
     *            请求
     * @return 响应
     */
    DemoResponse doSmthTimeout(DemoRequest req);

}
