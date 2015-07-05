package com.baidu.beidou.navi.pbrpc.client.ha;

/**
 * ClassName: FailFastStrategy <br/>
 * Function: 失败立即退出策略
 * 
 * @author Zhang Xu
 */
public class FailFastStrategy implements FailStrategy {

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.ha.FailStrategy#isQuitImmediately(int, int)
     */
    @Override
    public boolean isQuitImmediately(int currentRetryTime, int clientSize) {
        return true;
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.ha.FailStrategy#getMaxRetryTimes()
     */
    @Override
    public int getMaxRetryTimes() {
        return 1;
    }

}
