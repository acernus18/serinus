package org.maples.serinus.utility;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class SystemUtils {
    public static void getLocalNetInfo() {
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            log.info("{}", JSON.toJSONString(localHost));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
