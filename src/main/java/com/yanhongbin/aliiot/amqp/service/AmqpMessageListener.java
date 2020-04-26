package com.yanhongbin.aliiot.amqp.service;

import com.yanhongbin.aliiot.message.MessageProcess;
import lombok.Data;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Created with IDEA
 * description: Amqp Message 处理
 *
 * @author YanHongBin
 * @date Created in 2020/1/14 11:31
 */
@Data
public class AmqpMessageListener implements MessageListener {

    private Logger log = LoggerFactory.getLogger(getClass());

    private MessageProcess messageProcess;

    @SneakyThrows
    @Override
    public void onMessage(Message message) {
        System.out.println("当前线程："+Thread.currentThread().getName());
        byte[] body = message.getBody(byte[].class);
        String payload = new String(body);
        String topic = message.getStringProperty("topic");
        String messageId = message.getStringProperty("messageId");
        log.info("messageId:{}",messageId);
        log.info("topic:{}",topic);
        log.info("payload:{}",payload);
        messageProcess.processMessage(topic,payload);
    }
}
