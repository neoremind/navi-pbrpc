package com.baidu.beidou.navi.pbrpc.client.ha;

/**
 * ClassName: FailOverStrategy <br/>
 * Function: 失败重试策略
 * 
 * @author Zhang Xu
 */
public class FailOverStrategy implements FailStrategy {

    /**
     * 最大重试次数，默认为2
     */
    private int maxRetryTimes = 2;

    /**
     * Creates a new instance of FailOverStrategy.
     * 
     * @param maxRetryTimes
     */
    public FailOverStrategy(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.ha.FailStrategy#isQuitImmediately(int, int)
     */
    @Override
    public boolean isQuitImmediately(int currentRetryTime, int clientSize) {
        if (currentRetryTime + 1 == getMaxRetryTimes() || currentRetryTime + 1 == clientSize) {
            return true;
        }
        return false;
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.client.ha.FailStrategy#getMaxRetryTimes()
     */
    @Override
    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

}
