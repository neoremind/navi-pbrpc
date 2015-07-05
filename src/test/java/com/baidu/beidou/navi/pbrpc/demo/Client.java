package com.baidu.beidou.navi.pbrpc.demo;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.beidou.navi.pbrpc.client.PbrpcClient;
import com.baidu.beidou.navi.pbrpc.client.PbrpcClientFactory;
import com.baidu.beidou.navi.pbrpc.client.PooledConfiguration;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoBatchRequest;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoBatchResponse;
import com.baidu.beidou.navi.pbrpc.transport.PbrpcMsg;

/**
 * ClassName: Client <br/>
 * Function: 测试用客户端
 * 
 * @author Zhang Xu
 */
public class Client {

    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    /**
     * 调用服务端
     * 
     * @param port
     * @param multiSize
     *            并发数
     * @param invokeNum
     *            总请求数
     * @param size
     *            batch请求的数据内含的list数量
     * @param textLength
     *            batch请求数据中随机字符串的长度
     * @throws Exception
     */
    public void run(int port, int multiSize, int invokeNum, int size, int textLength)
            throws Exception {
        PbrpcClient client = PbrpcClientFactory.buildPooledConnection(new PooledConfiguration(),
                "127.0.0.1", port, 60000);
        ExecutorService pool = Executors.newFixedThreadPool(multiSize);
        CompletionService<DemoBatchResponse> completionService = new ExecutorCompletionService<DemoBatchResponse>(
                pool);

        BatchInvoker invoker = new BatchInvoker(client, size,
                RandomUtils.generateString(textLength));
        long time = System.currentTimeMillis();
        for (int i = 0; i < invokeNum; i++) {
            completionService.submit(invoker);
        }

        for (int i = 0; i < invokeNum; i++) {
            completionService.take().get();
        }

        long timetook = System.currentTimeMillis() - time;
        LOG.info("Send " + invokeNum + " requests using " + timetook + "ms");
        LOG.info("QPS:" + 1000f / ((timetook) / (1.0f * invokeNum)));
    }

    private static byte[] getBatchData(int size, String text) {
        DemoBatchRequest.Builder req = DemoBatchRequest.newBuilder();
        req.setRequestSize(size);
        req.setText(text);
        byte[] data = req.build().toByteArray();
        return data;
    }

    private class BatchInvoker implements Callable<DemoBatchResponse> {

        private PbrpcClient client;

        private int size;

        private String text;

        public BatchInvoker(PbrpcClient client, int size, String text) {
            this.client = client;
            this.size = size;
            this.text = text;
        }

        @Override
        public DemoBatchResponse call() throws Exception {
            PbrpcMsg msg;
            msg = new PbrpcMsg();
            msg.setServiceId(101);
            msg.setProvider("beidou");
            msg.setData(getBatchData(size, text));
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
