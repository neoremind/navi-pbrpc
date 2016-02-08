package com.baidu.beidou.navi.pbrpc.demo;

/**
 * ClassName: Main <br/>
 * Function: 压测用的Main入口
 * 
 * @author Zhang Xu
 */
public class Main {

    /**
     * main <br/>
     * 启用服务命令：
     * 
     * <pre>
     * java Main 8088
     * </pre>
     * 
     * 没有第二个参数表示启动服务端。<br/>
     * 启动测试客户端命令：
     * 
     * <pre>
     * java Main 8088 8 10000 20 500
     * </pre>
     * 
     * 标示调用本地8088端口，8个病房，10000个请求总计，返回请求的数据大小等于20*500=10k
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("args invalid");
            System.exit(-1);
        }
        int port = Integer.parseInt(args[0]);
        if (args.length == 1) {
            Server server = new Server();
            server.run(port);
        } else {
            int multiSize = Integer.parseInt(args[1]);
            int invokeNum = Integer.parseInt(args[2]);
            int size = Integer.parseInt(args[3]);
            int textLength = Integer.parseInt(args[4]);
            Client client = new Client();
            client.run(port, multiSize, invokeNum, size, textLength);
        }
    }

}
