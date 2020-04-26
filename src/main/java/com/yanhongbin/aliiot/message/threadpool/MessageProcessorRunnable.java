package com.yanhongbin.aliiot.message.threadpool;

import com.yanhongbin.aliiot.message.MessageProcess;
import com.yanhongbin.aliiot.message.service.MessageProcessor;

/**
 * Created with IDEA
 * description :
 *
 * @author ：yanhongbin
 * @date : Created in 2020/4/25 6:15 下午
 */
public class MessageProcessorRunnable implements Runnable {


    /**
     * 消息处理器
     */
    private MessageProcess messageProcess;

    /**
     * 标题
     */
    private String topic;

    /**
     * 消息体
     */
    private String payload;

    public MessageProcessorRunnable(MessageProcess messageProcess, String topic, String payload) {
        this.messageProcess = messageProcess;
        this.topic = topic;
        this.payload = payload;
    }



    @Override
    public void run() {
        messageProcess.processMessage(topic, payload);
    }
}
