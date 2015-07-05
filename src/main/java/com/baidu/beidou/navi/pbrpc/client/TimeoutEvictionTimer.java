package com.baidu.beidou.navi.pbrpc.client;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassName: TimeoutEvictionTimer <br/>
 * Function: 客户端超时调用检测器，对于超时的客户端回调后抛出异常{@link com.baidu.beidou.navi.pbrpc.exception.TimeoutException}
 * 
 * @author Zhang Xu
 */
public class TimeoutEvictionTimer {

    /**
     * timer
     */
    private static Timer timer;

    /**
     * 计数器
     */
    private static AtomicInteger usageCount = new AtomicInteger(0);

    /**
     * Creates a new instance of TimeoutEvictionTimer.
     */
    private TimeoutEvictionTimer() {
        // Hide the default constuctor
    }

    /**
     * Add the specified eviction task to the timer. Tasks that are added with a call to this method *must* call
     * {@link #cancel(TimerTask)} to cancel the task to prevent memory and/or thread leaks in application server
     * environments.
     * 
     * @param task
     *            Task to be scheduled
     * @param evictorDelayCheckMilliSeconds
     *            Delay in milliseconds before task is executed
     * @param evictorCheckPeriodMilliSeconds
     *            Time in milliseconds between executions
     */
    public static synchronized void schedule(TimerTask task, int evictorDelayCheckMilliSeconds,
            int evictorCheckPeriodMilliSeconds) {
        if (null == timer) {
            timer = new Timer(true);
        }
        usageCount.incrementAndGet();
        timer.schedule(task, evictorDelayCheckMilliSeconds, evictorCheckPeriodMilliSeconds);
    }

    /**
     * Remove the specified eviction task from the timer.
     * 
     * @param task
     *            Task to be scheduled
     */
    public static synchronized void cancel(TimerTask task) {
        if (task == null) {
            return;
        }
        task.cancel();
        usageCount.decrementAndGet();
        if (usageCount.get() == 0) {
            timer.cancel();
            timer = null;
        }
    }

}
