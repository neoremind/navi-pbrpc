package com.baidu.beidou.navi.pbrpc.client;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoBatchRequest;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoBatchResponse;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoRequest;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoResponse;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;

public class PooledPbrpcClientMainTest {

    private static final Logger LOG = LoggerFactory.getLogger(PooledPbrpcClientMainTest.class);

    public static void main(String[] args) throws Exception {
        PooledPbrpcClientMainTest test = new PooledPbrpcClientMainTest();
        test.testPool();
        // test.testPoolBatch();
    }

    public void testPool() throws Exception {
        PbrpcClient client = PbrpcClientFactory.buildPooledConnection(new PooledConfiguration(),
                "127.0.0.1", 8088, 4000);

        PbrpcMsg msg;
        msg = new PbrpcMsg();
        msg.setServiceId(100);
        msg.setProvider("beidou");
        msg.setData(getData(1));
        DemoResponse res = client.asyncTransport(DemoResponse.class, msg).get();
        System.out.println(res);

        int multiSize = 12;
        int totalRequestSize = 100000;
        ExecutorService pool = Executors.newFixedThreadPool(multiSize);
        CompletionService<DemoResponse> completionService = new ExecutorCompletionService<DemoResponse>(
                pool);

        Invoker invoker = new Invoker(client);
        long time = System.currentTimeMillis();
        for (int i = 0; i < totalRequestSize; i++) {
            completionService.submit(invoker);
        }

        for (int i = 0; i < totalRequestSize; i++) {
            completionService.take().get();
        }

        long timetook = System.currentTimeMillis() - time;
        LOG.info("Total using " + timetook + "ms");
        LOG.info("QPS:" + 1000f / ((timetook) / (1.0f * totalRequestSize)));
    }

    public void testPoolBatch() throws Exception {
        PbrpcClient client = PbrpcClientFactory.buildPooledConnection(new PooledConfiguration(),
                "127.0.0.1", 8088, 60000);
        int multiSize = 8;
        int totalRequestSize = 100;
        ExecutorService pool = Executors.newFixedThreadPool(multiSize);
        CompletionService<DemoBatchResponse> completionService = new ExecutorCompletionService<DemoBatchResponse>(
                pool);

        BatchInvoker invoker = new BatchInvoker(client);
        long time = System.currentTimeMillis();
        for (int i = 0; i < totalRequestSize; i++) {
            completionService.submit(invoker);
        }

        for (int i = 0; i < totalRequestSize; i++) {
            completionService.take().get();
        }

        long timetook = System.currentTimeMillis() - time;
        LOG.info("Total using " + timetook + "ms");
        LOG.info("QPS:" + 1000f / ((timetook) / (1.0f * totalRequestSize)));
    }

    private static byte[] getData(int userId) {
        DemoRequest.Builder req = DemoRequest.newBuilder();
        req.setUserId(userId);
        byte[] data = req.build().toByteArray();
        return data;
    }

    private class Invoker implements Callable<DemoResponse> {

        private PbrpcClient client;

        public Invoker(PbrpcClient client) {
            this.client = client;
        }

        @Override
        public DemoResponse call() throws Exception {
            PbrpcMsg msg;
            msg = new PbrpcMsg();
            msg.setServiceId(100);
            msg.setProvider("beidou");
            msg.setData(getData(1));
            DemoResponse res = client.asyncTransport(DemoResponse.class, msg).get();
            return res;
        }

    }

    private static byte[] getBatchData() {
        DemoBatchRequest.Builder req = DemoBatchRequest.newBuilder();
        req.setRequestSize(100);
        req.setText(RandomUtils.generateString(500));
        byte[] data = req.build().toByteArray();
        return data;
    }

    private class BatchInvoker implements Callable<DemoBatchResponse> {

        private PbrpcClient client;

        public BatchInvoker(PbrpcClient client) {
            this.client = client;
        }

        @Override
        public DemoBatchResponse call() throws Exception {
            PbrpcMsg msg;
            msg = new PbrpcMsg();
            msg.setServiceId(101);
            msg.setProvider("beidou");
            msg.setData(getBatchData());
            DemoBatchResponse res = client.asyncTransport(DemoBatchResponse.class, msg).get();
            return res;
        }

    }

    static class RandomUtils {

        public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static final String LETTERCHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static final String NUMBERCHAR = "0123456789";

        /**
         * 返回一个定长的随机字符串(只包含大小写字母、数字)
         * 
         * @param length
         *            随机字符串长度
         * @return 随机字符串
         */
        public static String generateString(int length) {
            StringBuffer sb = new StringBuffer();
            Random random = new Random();
            for (int i = 0; i < length; i++) {
                sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
            }
            return sb.toString();
        }

        /**
         * 返回一个定长的随机纯字母字符串(只包含大小写字母)
         * 
         * @param length
         *            随机字符串长度
         * @return 随机字符串
         */
        public static String generateMixString(int length) {
            StringBuffer sb = new StringBuffer();
            Random random = new Random();
            for (int i = 0; i < length; i++) {
                sb.append(ALLCHAR.charAt(random.nextInt(LETTERCHAR.length())));
            }
            return sb.toString();
        }

        /**
         * 返回一个定长的随机纯大写字母字符串(只包含大小写字母)
         * 
         * @param length
         *            随机字符串长度
         * @return 随机字符串
         */
        public static String generateLowerString(int length) {
            return generateMixString(length).toLowerCase();
        }

        /**
         * 返回一个定长的随机纯小写字母字符串(只包含大小写字母)
         * 
         * @param length
         *            随机字符串长度
         * @return 随机字符串
         */
        public static String generateUpperString(int length) {
            return generateMixString(length).toUpperCase();
        }

        /**
         * 生成一个定长的纯0字符串
         * 
         * @param length
         *            字符串长度
         * @return 纯0字符串
         */
        public static String generateZeroString(int length) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < length; i++) {
                sb.append('0');
            }
            return sb.toString();
        }

        /**
         * 根据数字生成一个定长的字符串，长度不够前面补0
         * 
         * @param num
         *            数字
         * @param fixdlenth
         *            字符串长度
         * @return 定长的字符串
         */
        public static String toFixdLengthString(long num, int fixdlenth) {
            StringBuffer sb = new StringBuffer();
            String strNum = String.valueOf(num);
            if (fixdlenth - strNum.length() >= 0) {
                sb.append(generateZeroString(fixdlenth - strNum.length()));
            } else {
                throw new RuntimeException("将数字" + num + "转化为长度为" + fixdlenth + "的字符串发生异常！");
            }
            sb.append(strNum);
            return sb.toString();
        }

        /**
         * 根据数字生成一个定长的字符串，长度不够前面补0
         * 
         * @param num
         *            数字
         * @param fixdlenth
         *            字符串长度
         * @return 定长的字符串
         */
        public static String toFixdLengthString(int num, int fixdlenth) {
            StringBuffer sb = new StringBuffer();
            String strNum = String.valueOf(num);
            if (fixdlenth - strNum.length() >= 0) {
                sb.append(generateZeroString(fixdlenth - strNum.length()));
            } else {
                throw new RuntimeException("将数字" + num + "转化为长度为" + fixdlenth + "的字符串发生异常！");
            }
            sb.append(strNum);
            return sb.toString();
        }

        public static void main(String[] args) {
            System.out.println(generateString(15));
            System.out.println(generateMixString(15));
            System.out.println(generateLowerString(15));
            System.out.println(generateUpperString(15));
            System.out.println(generateZeroString(15));
            System.out.println(toFixdLengthString(123, 15));
            System.out.println(toFixdLengthString(123L, 15));
        }
    }

}
