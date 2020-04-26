package com.yanhongbin.aliiot.amqp.util;

import com.yanhongbin.aliiot.amqp.enumerate.OsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created with IDEA
 * description: 获取服务器系统Mac地址
 *
 * @author YanHongBin
 * @date Created in 2020/1/14 12:59
 */
public class ClientIdUtil {

    private static Logger log = LoggerFactory.getLogger(ClientIdUtil.class);

    /**
     * 判断系统类型
     * @return
     */
    private static OsType getOsType(){
        String osName = System.getProperty("os.name");
        System.out.println(osName);
        if (osName.startsWith("Mac OS")) {
            // 苹果
            return OsType.MACOS;
        } else if (osName.startsWith("Windows")) {
            // windows
            return OsType.WINDOWS;
        } else {
            // unix or linux
            return OsType.LINUX_UNIX;
        }
    }

    /**
     * 获取Linux系统Mac地址
     * @return String
     */
    private static String getAddressByLinux() throws IOException {
        String str1 = CommandUtil.exec("ifconfig");
        String str2 = str1.split("ether")[1].trim();
        String result = str2.split("txqueuelen")[0].trim();
        log.info("Linux MacAddress is: {}", result);
        return result;
    }

    private static String getAddressByMacOs() throws IOException {
        String result = CommandUtil.exec("ifconfig en0");
        String[] ethers = result.split("ether");
        String[] inet6s = ethers[1].split("inet6");
        result = inet6s[0].trim();
        log.info("Mac os MacAddress is: {}", result);
        return result;
    }

    /**
     * 获取Windows系统Mac地址
     * @return String
     */
    private static String getMacAddressByWindows() throws IOException, InterruptedException {
        String result = "";
        Process process = Runtime.getRuntime().exec("ipconfig /all");
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
        String line;
        int index = -1;
        while ((line = br.readLine()) != null) {
            index = line.toLowerCase().indexOf("物理地址");
            if (index >= 0) {
                index = line.indexOf(":");
                if (index >= 0) {
                    result = line.substring(index + 1).trim();
                }
                break;
            }
         }
        log.info("Windows MACAddress is: {}", result);
        br.close();
        return result;
    }

    /**
     * 获取系统mac地址,支持linux和windows系统,mac返回UUID
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static String getMacAddress() throws IOException, InterruptedException {
        OsType osType = getOsType();
        switch (osType) {
            case LINUX_UNIX:
                return getAddressByLinux();
            case WINDOWS:
                return getMacAddressByWindows();
            case MACOS:
                return getAddressByMacOs();
            default:
                return "";
        }

    }
}
