package com.yanhongbin.aliiot.amqp;

import com.yanhongbin.aliiot.amqp.service.AmqpMessageListener;
import com.yanhongbin.aliiot.amqp.util.ClientIdUtil;
import com.yanhongbin.aliiot.amqp.util.SignUtil;
import com.yanhongbin.aliiot.config.AliIotProperties;
import com.yanhongbin.aliiot.config.AmqpProperties;
import com.yanhongbin.aliiot.config.ConnectConfig;
import com.yanhongbin.aliiot.config.ConsumerGroupMessageConfig;
import com.yanhongbin.aliiot.config.enumerate.ConnectSetting;
import com.yanhongbin.aliiot.config.enumerate.SubscribeSwitch;
import com.yanhongbin.aliiot.message.MessageProcess;
import com.yanhongbin.aliiot.message.service.MessageProcessor;
import com.yanhongbin.aliiot.utils.SpringUtil;
import org.apache.qpid.jms.JmsConnectionListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * Created with IDEA
 * description: 多个Amqp监听启动类,根据消费组ID
 *
 * @author YanHongBin
 * @date Created in 2020/1/14 13:45
 */
@Component
@Scope("singleton")
public class AmqpStarter {

    /**
     * 设备id
     */
    private static String clientId;

    private static AmqpManager[] amqpManagers;

    static {
        // 初始化设备ID
        try {
            clientId = ClientIdUtil.getMacAddress();
        } catch (IOException |InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Resource(type = SpringUtil.class)
    private SpringUtil springUtil;

    @Resource(type = ConnectConfig.class)
    private ConnectConfig connectConfig;

    @Resource(type = AliIotProperties.class)
    private AliIotProperties aliIotProperties;

    @Resource(type = AmqpProperties.class)
    private AmqpProperties amqpProperties;

    @Resource(type = JmsConnectionListener.class)
    private JmsConnectionListener jmsConnectionListener;

    private String signMethod;

    @PostConstruct
    public void init() {
        SubscribeSwitch subscribeSwitch = connectConfig.getSubscribeSwitch();
        if (subscribeSwitch == null || subscribeSwitch == SubscribeSwitch.OFF) {
            // 关闭服务端订阅功能,不需要初始化
            return;
        }

        if (connectConfig.getType() != ConnectSetting.AMQP) {
            // 不是AMQP,不初始化
            return;
        }
        // 初始化签名生成方法
        if (amqpProperties.getSignMethod() != null) {
            signMethod = amqpProperties.getSignMethod().toString();
        }
    }

    /**
     * 开启多个amqp监听线程
     * @throws NamingException throws by {@link AmqpStarter#getContext()},{@link Context#lookup(String)}
     * @throws NoSuchAlgorithmException throws by {@link javax.crypto.Mac#getInstance(String)}
     * @throws InvalidKeyException throws by {@link javax.crypto.Mac#init(Key)}
     */
    public void startAmqp() throws NamingException, NoSuchAlgorithmException, InvalidKeyException {
        long timeStamp = System.currentTimeMillis();
        Context context = getContext();
        ConnectionFactory cf = (ConnectionFactory)context.lookup("SBCF");
        Destination queue = (Destination)context.lookup("QUEUE");
        ConsumerGroupMessageConfig[] consumerGroupMessageConfig = amqpProperties.getConsumerGroupMessageConfig();

        amqpManagers = new AmqpManager[consumerGroupMessageConfig.length];
        for (int i = 0; i < consumerGroupMessageConfig.length; i++) {
            AmqpManager amqpManager = new AmqpManager();
            amqpManager.setCf(cf);
            amqpManager.setJmsConnectionListener(jmsConnectionListener);
            amqpManager.setQueue(queue);
            amqpManager.setPassword(getPassword(timeStamp));
            amqpManager.setUsername(getUserName(timeStamp, consumerGroupMessageConfig[i].getConsumerGroupId()));

            if (!StringUtils.isEmpty(consumerGroupMessageConfig[i].getMessageProcessorBeanId())) {
                // 配置了MessageProcessor的情况下,使用配置的MessageProcessor
                amqpManager.setMessageProcessor((MessageProcessor) SpringUtil.getBean(consumerGroupMessageConfig[i].getMessageProcessorBeanId()));
            }else{
                // 未配置使用默认的注入的MessageProcessor
                amqpManager.setMessageProcessor(SpringUtil.getBean(MessageProcessor.class));
            }
            // 多例bean 手动注入MessageProcessor
            MessageProcess messageProcess = SpringUtil.getBean(MessageProcess.class);
            messageProcess.setMessageProcessor(amqpManager.getMessageProcessor());
            // 手动生成AmqpMessageListener以匹配不同的MessageProcessor
            AmqpMessageListener amqpMessageListener = new AmqpMessageListener();
            amqpMessageListener.setMessageProcess(messageProcess);
            amqpManager.setMessageListener(amqpMessageListener);
            amqpManagers[i] = amqpManager;
        }
        // 启动线程
        Arrays.asList(amqpManagers).forEach(Thread::start);
    }

    private String getUserName(long timeStamp,String consumerGroupId) {
        return clientId + "|authMode=aksign"
                + ",signMethod=" + signMethod
                + ",timestamp=" + timeStamp
                + ",authId=" + aliIotProperties.getAccessKeyId()
                + ",consumerGroupId=" + consumerGroupId
                + "|";
    }

    private String getPassword(long timeStamp) throws InvalidKeyException, NoSuchAlgorithmException {
        String signContent = "authId=" + aliIotProperties.getAccessKeyId() + "&timestamp=" + timeStamp;
        return SignUtil.doSign(signContent, aliIotProperties.getAccessKeySecret(), signMethod);
    }

    private String getConnectionUrl(){
         return  "failover:(amqps://"+aliIotProperties.getUid()+".iot-amqp."+aliIotProperties.getRegionId().toString()+".aliyuncs.com:5671?amqp.idleTimeout=80000)"
                + "?failover.reconnectDelay=30";
    }

    private Context getContext() throws NamingException {
        Hashtable<String, String> hashtable = new Hashtable<>();
        hashtable.put("connectionfactory.SBCF",getConnectionUrl());
        hashtable.put("queue.QUEUE", "default");
        hashtable.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        return new InitialContext(hashtable);
    }

    public void destroyBean() {
        Arrays.asList(amqpManagers).forEach(AmqpManager::close);
    }
}
