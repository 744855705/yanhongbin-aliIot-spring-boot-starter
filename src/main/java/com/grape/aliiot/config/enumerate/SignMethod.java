package com.grape.aliiot.config.enumerate;

/**
 * Created with IDEA
 * description: Amqp签名方法
 *
 * @author YanHongBin
 * @date Created in 2020/1/14 11:53
 */
public enum SignMethod {

    //签名方法：支持hmacmd5，hmacsha1和hmacsha256
    HMACMD5("hmacmd5"),
    HMACSHA1("hmacsha1"),
    HMACSHA256("hmacsha256"),
    ;
    private String name;

    SignMethod(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
