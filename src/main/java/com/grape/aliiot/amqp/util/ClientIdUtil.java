package com.grape.aliiot.amqp.util;

import com.grape.aliiot.amqp.enumerate.OsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * Created with IDEA
 * description:
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
    private static String geacAddressByLinux() throws IOException, InterruptedException {
        String[] cmd = {"ifconfig"};
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
             sb.append(line);
        }
        String str1 = sb.toString();
        String str2 = str1.split("ether")[1].trim();
        String result = str2.split("txqueuelen")[0].trim();
        log.info("Linux MacAddress is: {}", result);
        br.close();
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
                return geacAddressByLinux();
            case WINDOWS:
                return getMacAddressByWindows();
            case MACOS:
                return UUID.randomUUID().toString();
            default:
                return "";
        }
    }
}
