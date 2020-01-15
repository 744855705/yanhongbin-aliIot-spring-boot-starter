package com.grape.aliiot.message.service.impl;

import com.grape.aliiot.message.service.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created with IDEA
 * description: 默认消息处理器
 *
 * @author YanHongBin
 * @date Created in 2020/1/13 14:11
 */
@Service
public class DefaultMessageProcessor implements MessageProcessor {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void processOnOffLineMessage(String topic, String payload) {
        log.info("处理上线下线消息");
        log.info("topic:{}",topic);
        log.info("payload:{}",payload);
    }

    @Override
    public void processNormalMessage(String topic, String payload) {
        log.info("处理普通消息");
        log.info("topic:{}",topic);
        log.info("payload:{}",payload);
    }
}
