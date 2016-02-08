package com.baidu.beidou.navi.pbrpc.client;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.baidu.beidou.navi.pbrpc.client.ha.TransportCallback;

/**
 * 健康状态监控器，实现{@link TransportCallback}接口，在{@link com.baidu.beidou.navi.pbrpc.client.ha.LoadBalanceStrategy}中用做回调callback
 * <p/>
 * 该回调的特点是，由于{@link HAPbrpcClient}不管成功、失败都会显示调用callback，因此使之具备记录、积累一些信息的能力，这里主要场景是自动剔除失效连接客户端。
 * <p/>
 * 长期失效的远程服务需要自动剔除对应的客户端{@link PbrpcClient}，
 * 这里的策略是在指定的时间段内，记录某个远程服务调用的成功、失败次数，然后计算失败率，超过一定比率则永久剔除。
 *
 * @author zhangxu
 */
public class AutoEvictTransportCallback implements TransportCallback, InitializingBean, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(AutoEvictTransportCallback.class);

    /**
     * 线程安全的客户端监控dict
     * <p/>
     * key为客户端的描述信息，例如IP:PORT等；value是监控状态信息
     */
    private ConcurrentHashMap<String, MonitorStatus> counter = new ConcurrentHashMap<String, MonitorStatus>();

    /**
     * 单位时间内可允许的出错调用占所有调用总数的最大比例，按照百分比格式，范围为0-100。
     */
    private int maxFailPercentage = 80;

    /**
     * 单位时间内最小的调用次数，只有超过这个数字才会启用自动剔除策略。
     * <p/>
     * 这里主要防止“饿死”的误杀，比如单位时间只有一次调用，恰巧失败了确被剔除了，有失公平和准确。
     */
    private int minInvokeNumber = 10;

    /**
     * 单位时间段，单位毫秒
     * <p/>
     * 健康检查在这个时间段内计算失败率，目前策略是下一个周期则清空上个周期的结果，不累计数据
     */
    private long checkPeriod = 600000L;

    /**
     * 启用健康检查的delay时间，单位毫秒
     * <p/>
     * 理论上，服务器启用该时间后，第一次运行健康监控检测程序
     */
    private long initDelay = 60000L;

    /**
     * 周期执行器，利用Spring组件
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void afterPropertiesSet() {
        this.scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                new Evictor().doEvict();
            }
        }, this.initDelay, this.checkPeriod, TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy() {
        this.scheduler.shutdown();
        try {
            this.scheduler.awaitTermination(3600L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        }
    }

    /**
     * 客户端调用成功后的回调
     *
     * @param currClient 当前调用成功的客户端引用
     * @param clientList 当前整个{@link com.baidu.beidou.navi.pbrpc.client.HAPbrpcClient}托管的客户端列表
     */
    @Override
    public void onSuccess(PbrpcClient currClient, List<PbrpcClient> clientList) {
        counter.putIfAbsent(currClient.getInfo(), new MonitorStatus(currClient, clientList));
        counter.get(currClient.getInfo()).successCount.incrementAndGet();
    }

    /**
     * 客户端调用失败后的回调
     *
     * @param currClient 当前调用成功的客户端引用
     * @param clientList 当前整个{@link com.baidu.beidou.navi.pbrpc.client.HAPbrpcClient}托管的客户端列表
     * @param e          调用错误异常
     */
    @Override
    public void onFail(PbrpcClient currClient, List<PbrpcClient> clientList, Exception e) {
        counter.putIfAbsent(currClient.getInfo(), new MonitorStatus(currClient, clientList));
        counter.get(currClient.getInfo()).failCount.incrementAndGet();
    }

    /**
     * 健康监控信息，包含单位时间内调用成功、失败次数，监控的客户端等
     */
    class MonitorStatus {

        /**
         * 单位时间累计成功次数
         */
        AtomicLong successCount = new AtomicLong(0);

        /**
         * 单位时间累计失败次数
         */
        AtomicLong failCount = new AtomicLong(0);

        /**
         * {@link HAPbrpcClient}托管的客户端列表
         */
        List<PbrpcClient> clientList;

        /**
         * 监控的客户端
         */
        PbrpcClient currClient;

        /**
         * 构造函数
         *
         * @param currClient 监控的客户端
         * @param clientList {@link HAPbrpcClient}托管的客户端列表
         */
        public MonitorStatus(PbrpcClient currClient, List<PbrpcClient> clientList) {
            this.currClient = currClient;
            this.clientList = clientList;
        }

    }

    /**
     * 失效链接剔除策略实现
     */
    class Evictor {

        /**
         * 自动剔除失效客户端
         */
        void doEvict() {
            try {
                LOG.info(String.format("Health profiling info of pbrpc clients, checkPeriod=%dms, "
                        + "maxFailPercentage=%s", checkPeriod, calculatePercentage(maxFailPercentage, 100)));
                System.out.println(
                        "-----------------------------------------------------------------------------------------------------------");
                System.out.println(
                        "|                        client                       |   total   |   succ   |   fail   |  "
                                + "fail percentage |");
                System.out.println(
                        "-----------------------------------------------------------------------------------------------------------");
                for (Map.Entry<String, MonitorStatus> entry : counter.entrySet()) {
                    String clientInfo = entry.getKey();
                    MonitorStatus status = entry.getValue();
                    long succCount = status.successCount.longValue();
                    long failCount = status.failCount.longValue();
                    long totalCount = succCount + failCount;
                    System.out.println(
                            String.format(" %s      %d           %d           %d          %s", clientInfo, totalCount,
                                    succCount, failCount, calculatePercentage(failCount, totalCount)));
                }
                System.out.println(
                        "-----------------------------------------------------------------------------------------------------------");

                for (Map.Entry<String, MonitorStatus> entry : counter.entrySet()) {
                    String clientInfo = entry.getKey();
                    MonitorStatus status = entry.getValue();
                    long totalCount = status.successCount.longValue() + status.failCount.longValue();
                    float failPercentage = (float) status.failCount.longValue() / (float) totalCount;
                    if (totalCount > minInvokeNumber
                            && status.clientList.size() > 1
                            && failPercentage * 100f > maxFailPercentage) {
                        for (int i = 0; i < status.clientList.size(); i++) {
                            PbrpcClient client = status.clientList.get(i);
                            if (client.getInfo().equals(clientInfo)) {
                                client.shutdown();
                                status.clientList.remove(client);
                                LOG.info("Remove " + clientInfo + " due to fail percentage is "
                                        + failPercentage * 100f + "% which is greater than "
                                        + "maxFailPercentage=" + calculatePercentage(maxFailPercentage, 100));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("Error occurred when evicting due to " + e.getMessage(), e);
            } finally {
                // 清理计数器
                counter.clear();
            }
        }

    }

    /**
     * 计算百分比，例如num=10，total=100，返回10.00%字符串
     *
     * @param num   分子
     * @param total 分母
     *
     * @return 百分比字符串
     */
    private String calculatePercentage(long num, long total) {
        float numInFloat = num * 1.0f;
        float result = numInFloat / total;
        DecimalFormat df = new DecimalFormat("0.00%");//##.00%百分比格式，后面不足2位的用0补齐
        return df.format(result);
    }

    public void setMaxFailPercentage(int maxFailPercentage) {
        this.maxFailPercentage = maxFailPercentage;
    }

    public void setCheckPeriod(long checkPeriod) {
        this.checkPeriod = checkPeriod;
    }

    public void setInitDelay(long initDelay) {
        this.initDelay = initDelay;
    }

    public void setMinInvokeNumber(int minInvokeNumber) {
        this.minInvokeNumber = minInvokeNumber;
    }
}
