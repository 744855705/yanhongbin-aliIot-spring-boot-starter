package com.yanhongbin.aliiot.amqp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;


/**
 * @author yanhongbin
 * des： 执行shell命令
 * date：2020/04/02
 */
public class CommandUtil {

    /**
     * 执行shell命令并返回{@link String}类型的返回值
     *
     * @param command 命令内容
     * @return String
     * @throws IOException              命令不存在会抛出，读取shell返回值可能导致I/O异常
     * @throws IllegalArgumentException 命令为空时抛出
     */
    public static String exec(String command) throws IOException, IllegalArgumentException {
        InputStream in = null;
        InputStreamReader isr = null;
        BufferedReader buffer = null;
        try {
            Process pwd = Runtime.getRuntime().exec(command);
            in = pwd.getInputStream();
            // 配置编码类型为GBK，防止控制台返回的中文乱码
            isr = new InputStreamReader(in, Charset.forName("GBK"));
            buffer = new BufferedReader(isr);
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = buffer.readLine()) != null) {
                // 读取拼接返回值
                builder.append(line);
                builder.append("\n");
            }
            int length = builder.length();
            if (length > 0) {
                // 返回值不为空的时候，去掉最后一个换行
                builder.deleteCharAt(length - 1);
            }
            return builder.toString();
        } finally {
            // 关闭资源
            if (in != null) {
                in.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (buffer != null) {
                buffer.close();
            }
        }
    }

}
