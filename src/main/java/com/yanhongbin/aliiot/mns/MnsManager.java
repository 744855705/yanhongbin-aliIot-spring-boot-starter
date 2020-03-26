package com.yanhongbin.aliiot.mns;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.yanhongbin.aliiot.config.AliIotProperties;
import com.yanhongbin.aliiot.message.MessageProcess;
import com.yanhongbin.aliiot.message.service.MessageProcessor;
import com.yanhongbin.aliiot.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;


/**
 * Created with IDEA
 * description:Mns监听启动类
 * @author :YanHongBin
 * @date :Created in 2019/8/6 10:43
 */
@Slf4j
public class MnsManager extends Thread{

    /**
     * 队列名
     */
    private String queueName;

    /**
     * MNSClient,用于开启连接
     */
    private MNSClient client;


    private String productKey;

    private AliIotProperties aliIotProperties;

    public MnsManager(MNSClient client,String queueName,String productKey){
        this.client = client;
        this.queueName = queueName;
        this.productKey = productKey;
    }

//    @Resource(type = MessageProcess.class)
    private MessageProcess messageProcess;

    @Override
    public void run() {
        initMessageProcess();
        CloudQueue queue = client.getQueueRef(queueName);
        while (MnsStarter.flag) {
            com.aliyun.mns.model.Message popMsg = null;
            try {
                // 获取消息
                popMsg = queue.popMessage(10);
                if (popMsg != null) {
                    String messageBody = popMsg.getMessageBody();
                    // 处理消息
                    processMessage(MnsMessage.parseMnsMessage(messageBody));
                    // 处理后从队列中删除消息
                    queue.deleteMessage(popMsg.getReceiptHandle());
                }
            }catch (Exception e){
                // 发生异常不终止处理消息
                e.printStackTrace();
                log.info("处理MNS消息发生异常"+ popMsg);
            }
        }
        // flag 为 false 之后,关闭消息队列监听
        client.close();

    }

    private void initMessageProcess() {
        // 初始化messageProcess
        messageProcess = SpringUtil.getBean(MessageProcess.class);
        HashMap<String, String> messageProcessorBeanIdMap = aliIotProperties.getMessageProcessorBeanId();
        String messageProcessorBeanId = messageProcessorBeanIdMap.get(productKey);
        if (StringUtils.isEmpty(messageProcessorBeanId)) {
            // 未配置消息处理器,使用默认的消息处理器
            messageProcess.setMessageProcessor(SpringUtil.getBean(MessageProcessor.class));
        }else{
            messageProcess.setMessageProcessor((MessageProcessor) SpringUtil.getBean(messageProcessorBeanId));
        }
    }

    /**
     * 处理收到的消息
     * @param mnsMessage mns消息封装
     */
    public void processMessage(MnsMessage mnsMessage) {
        String messageId = mnsMessage.getMessageId();
        String topic = mnsMessage.getTopic();
        String payload = mnsMessage.getPayload();
        log.info("messageId:{}",messageId);
        log.info("topic:{}",topic);
        log.info("payload:{}",payload);
        messageProcess.processMessage(topic, payload);
    }

    /**
     * 将阿里平台配置信息放入实体类中
     * @param aliIotProperties 配置信息
     */
    public void setAliIotProperties(AliIotProperties aliIotProperties) {
        this.aliIotProperties = aliIotProperties;
    }
}
