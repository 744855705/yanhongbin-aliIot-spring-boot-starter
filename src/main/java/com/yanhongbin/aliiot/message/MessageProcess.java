package com.yanhongbin.aliiot.message;

import com.yanhongbin.aliiot.config.AliIotProperties;
import com.yanhongbin.aliiot.exception.BeanInitException;
import com.yanhongbin.aliiot.message.service.MessageProcessor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Created with IDEA
 * description: 消息处理,多例Bean,适配多个连接的情况下处理消息
 *
 * @author YanHongBin
 * @date Created in 2020/1/13 14:01
 */
@Component
@Scope("prototype")
public class MessageProcess {

    @Resource(type = AliIotProperties.class)
    private AliIotProperties aliIotProperties;

    /**
     * 消息处理器
     */
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


    public void setMessageProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }
}
