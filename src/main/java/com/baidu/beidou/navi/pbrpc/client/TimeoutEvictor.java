package com.baidu.beidou.navi.pbrpc.client;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.client.callback.CallbackContext;
import com.baidu.beidou.navi.pbrpc.client.callback.CallbackPool;
import com.baidu.beidou.navi.pbrpc.exception.TimeoutException;

/**
 * ClassName: TimeoutEvictor <br/>
 * Function: 检测连接超时的客户端，回调抛出异常{@link com.baidu.beidou.navi.pbrpc.exception.TimeoutException}
 * 
 * @author Zhang Xu
 */
public class TimeoutEvictor extends TimerTask {

    private static final Logger LOG = LoggerFactory.getLogger(TimeoutEvictor.class);

    /**
     * 运行超时检测器
     * 
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        try {
            detectTimetout();
        } catch (Exception e) {
            // ignored
        }
    }

    /**
     * 检测超时
     */
    @SuppressWarnings("unused")
    private synchronized void detectTimetout() {
        long start = System.nanoTime();
        try {
            int totalScanned = 0;
            int totalTimeouted = 0;
            ConcurrentMap<Integer, CallbackContext> map = CallbackPool.getCALLBACK_MAP();
            if (map != null && !map.isEmpty()) {
                List<CallbackContext> list = new ArrayList<CallbackContext>(map.values());
                for (CallbackContext cc : list) {
                    if (System.currentTimeMillis() - cc.getStartTime() > cc.getTimeout()) {
                        cc.getCallback().handleError(
                                new TimeoutException("Client call timeout, request logId="
                                        + cc.getLogId()));
                        CallbackContext context = CallbackPool.getContext(cc.getLogId());
                        if (context != null && context.isShortAliveConn()) {
                            if (context.getChannel() != null) {
                                LOG.info(String.format("Close channel %s, logId=%s",
                                        context.getChannel(), context.getLogId()));
                                context.getChannel().close();
                            }
                        }
                        CallbackPool.remove(cc.getLogId());
                        totalTimeouted++;
                    }
                }
                totalScanned = list.size();
            }
            // LOG.info("Detecting timeout done using " + (System.nanoTime() - start) / 1000
            // + "us, totalScanned=" + totalScanned + " ,totalTimeouted=" + totalTimeouted);
        } catch (Exception e) {
            LOG.warn("Exception occurred when detecting timeout callbacks", e);
        }
    }
}
