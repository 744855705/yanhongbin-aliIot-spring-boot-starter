package com.grape.aliiot.message;

import com.grape.aliiot.config.AliIotProperties;
import com.grape.aliiot.exception.BeanInitException;
import com.grape.aliiot.message.service.MessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Created with IDEA
 * description: 消息处理,单例Bean
 *
 * @author YanHongBin
 * @date Created in 2020/1/13 14:01
 */
@Component
@Scope("singleton")
public class MessageProcess {

    @Resource(type = AliIotProperties.class)
    private AliIotProperties aliIotProperties;

    @Resource(type = MessageProcessor.class)
    private MessageProcessor messageProcessor;

    private String statusTopicHeader;

    @PostConstruct
    public void init() throws Exception{
        if (aliIotProperties != null) {
            statusTopicHeader = "/as/mqtt/status/";
        }else{
            throw new BeanInitException("aliIotProperties 注入失败");
        }
    }

    /**
     * 处理消息
     * @param topic 标题
     * @param payload 消息体
     */
    public void processMessage(String topic,String payload) {

        if (topic.startsWith(statusTopicHeader)) {
            messageProcessor.processOnOffLineMessage(topic, payload);
            return;
        }
        // 处理普通消息
        messageProcessor.processNormalMessage(topic, payload);
    }
}
