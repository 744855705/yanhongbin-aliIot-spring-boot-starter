package com.grape.aliiot.message.service;

import org.springframework.stereotype.Component;


/**
 * Created with IDEA
 * description:
 *
 * @author YanHongBin
 * @date Created in 2020/1/13 14:07
 */
@Component
public interface MessageProcessor {


    /**
     * 处理上下线消息
     * @param topic 消息标题
     * @param payload 消息体
     */
    void processOnOffLineMessage(String topic,String payload);


    /**
     * 处理普通消息
     * @param topic 消息标题
     * @param payload 消息体
     */
    void processNormalMessage(String topic,String payload);
}
