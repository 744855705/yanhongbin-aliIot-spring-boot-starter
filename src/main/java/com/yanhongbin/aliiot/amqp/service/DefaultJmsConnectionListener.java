package com.yanhongbin.aliiot.amqp.service;

import org.apache.qpid.jms.JmsConnectionListener;
import org.apache.qpid.jms.message.JmsInboundMessageDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.net.URI;

/**
 * Created with IDEA
 * description: 默认Amqp连接监听类
 * @author YanHongBin
 * @date Created in 2020/1/14 11:42
 */
@Component
@Scope("singleton")
public class DefaultJmsConnectionListener implements JmsConnectionListener {

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 连接成功建立。
     */
    @Override
    public void onConnectionEstablished(URI remoteURI) {
        log.info("连接成功建立,onConnectionEstablished, remoteUri:{}", remoteURI);
    }

    /**
     * 尝试过最大重试次数之后，最终连接失败。
     */
    @Override
    public void onConnectionFailure(Throwable error) {
        log.error("连接失败,onConnectionFailure, {}", error.getMessage());
    }

    /**
     * 连接中断。
     */
    @Override
    public void onConnectionInterrupted(URI remoteURI) {
        log.info("连接中断,onConnectionInterrupted, remoteUri:{}", remoteURI);
    }

    /**
     * 连接中断后又自动重连上。
     */
    @Override
    public void onConnectionRestored(URI remoteURI) {
        log.info("重新连接,onConnectionRestored, remoteUri:{}", remoteURI);
    }

    @Override
    public void onInboundMessage(JmsInboundMessageDispatch envelope) {}

    @Override
    public void onSessionClosed(Session session, Throwable cause) {}

    @Override
    public void onConsumerClosed(MessageConsumer consumer, Throwable cause) {}

    @Override
    public void onProducerClosed(MessageProducer producer, Throwable cause) {}

}
