package com.baidu.beidou.navi.pbrpc.client.ha;

import java.util.ArrayList;
import java.util.List;

import com.baidu.beidou.navi.pbrpc.exception.client.HAPbrpcException;
import com.baidu.beidou.navi.pbrpc.util.StringPool;

/**
 * ClassName: ConnectStringParser <br/>
 * Function: 连接字符串的解析器，用于解析例如<tt>1.1.1.1:8080,2.2.2.2:9999</tt>类似的参数并且转换为{@link IpPort}对象列表
 * 
 * @author Zhang Xu
 */
public class ConnectStringParser {

    /**
     * 解析连接字符串，例如<tt>1.1.1.1:8080,2.2.2.2:9999</tt>类似的参数，转换为{@link IpPort}对象列表
     * 
     * @param connectString
     * @return List<IpPort>
     */
    public static List<IpPort> resolveConnectString(String connectString) {
        if (connectString == null || connectString.length() == 0 || connectString.equals("")) {
            throw new HAPbrpcException("connect string should not be empty");
        }
        List<IpPort> ret = new ArrayList<IpPort>(8);
        String[] pairs = connectString.split(StringPool.Symbol.COMMA);
        for (String pair : pairs) {
            String[] arr = pair.split(StringPool.Symbol.COLON);
            if (arr.length != 2) {
                throw new HAPbrpcException(
                        "connect string is invalid, string should be split by comma like 1.1.1.1:8088,2.2.2.2:8099");
            }
            String ip = arr[0];
            int port = 0;
            try {
                port = Integer.parseInt(arr[1]);
            } catch (NumberFormatException e) {
                throw new HAPbrpcException("connect string is invalid, port should be a number");
            }

            IpPort ipPort = new IpPort(ip, port);
            ret.add(ipPort);
        }
        return ret;
    }

}
