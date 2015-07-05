package com.baidu.beidou.navi.pbrpc.client.ha;

/**
 * ClassName: FailStrategy <br/>
 * Function: 失败处理策略
 * 
 * @author Zhang Xu
 */
public interface FailStrategy {

    /**
     * 失败后是否立即退出
     * 
     * @param currentRetryTime
     *            当前已经重试的次数，从0开始
     * @param clientSize
     *            客户端IP:PORT的可用数量
     * @return 是否立即退出
     */
    boolean isQuitImmediately(int currentRetryTime, int clientSize);

    /**
     * 获取最大的重试次数
     * 
     * @return
     */
    int getMaxRetryTimes();

}
