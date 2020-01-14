package com.grape.aliiot.config.enumerate;

/**
 * Created with IDEA
 * description:
 *
 * @author YanHongBin
 * @date Created in 2020/1/13 17:15
 */
public enum ConnectSetting {
    /**
     * 使用阿里提供的MNS消息服务
     */
    MNS,
    /**
     * 使用阿里iot-client-message 提供的http2长连接支持
     */
    HTTP2,
    /**
     * AMQP推送
     */
    AMQP,
    ;
}
