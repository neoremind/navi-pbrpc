package com.baidu.beidou.navi.pbrpc.client;

import java.io.IOException;
import java.io.InputStream;

import com.baidu.beidou.navi.pbrpc.protocol.Header;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;

/**
 * ClassName: HeaderResolver <br/>
 * Function: 头解析构造器，仅用户blocking io半双工场景
 * 
 * @author Zhang Xu
 */
public interface HeaderResolver {

    /**
     * 从响应头中解析头
     * 
     * @param in
     * @return
     * @throws IOException
     */
    Header resolveResHeader(InputStream in) throws IOException;

    /**
     * 根据头信息以及响应流读取响应体
     * 
     * @param header
     * @param in
     * @return
     * @throws IOException
     */
    byte[] resolveResBodyByResHeader(Header header, InputStream in) throws IOException;

    /**
     * 构造发送请求的头和体
     * 
     * @param pbrpcMsg
     * @return
     * @throws IOException
     */
    byte[] packReqHeaderAndBody(PbrpcMsg pbrpcMsg) throws IOException;

}
