package com.yanhongbin.aliiot.exception;

/**
 * Created with IDEA
 * description: 配置信息不全时抛出此异常
 *
 * @author YanHongBin
 * @date Created in 2020/1/13 11:40
 */
public class PropertiesNotFoundException extends Exception {

    public PropertiesNotFoundException(String message) {
        super(message);
    }
}
