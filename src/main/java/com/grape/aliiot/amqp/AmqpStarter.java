package com.grape.aliiot.amqp;

import com.grape.aliiot.amqp.service.AmqpMessageListener;
import com.grape.aliiot.amqp.service.DefaultJmsConnectionListener;
import com.grape.aliiot.amqp.util.ClientIdUtil;
import com.grape.aliiot.amqp.util.SignUtil;
import com.grape.aliiot.config.AliIotProperties;
import com.grape.aliiot.config.AmqpProperties;
import com.grape.aliiot.config.ConnectConfig;
import com.grape.aliiot.config.enumerate.ConnectSetting;
import com.grape.aliiot.config.enumerate.SubscribeSwitch;
import org.apache.qpid.jms.JmsConnection;
import org.apache.qpid.jms.JmsConnectionListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * Created with IDEA
 * description:
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

    @Resource(type = ConnectConfig.class)
    private ConnectConfig connectConfig;

    @Resource(type = AliIotProperties.class)
    private AliIotProperties aliIotProperties;

    @Resource(type = AmqpProperties.class)
    private AmqpProperties amqpProperties;

    @Resource(type = JmsConnectionListener.class)
    private JmsConnectionListener jmsConnectionListener;

    @Resource(type = MessageListener.class)
    private MessageListener messageListener;

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

    public void startAmqp() throws Exception {
        long timeStamp = System.currentTimeMillis();
        Context context = getContext();
        ConnectionFactory cf = (ConnectionFactory)context.lookup("SBCF");
        Destination queue = (Destination)context.lookup("QUEUE");
        String[] consumerGroupIds = amqpProperties.getConsumerGroupId();
        amqpManagers = new AmqpManager[consumerGroupIds.length];
        for (int i = 0; i < consumerGroupIds.length; i++) {
            AmqpManager amqpManager = new AmqpManager();
            amqpManager.setCf(cf);
            amqpManager.setJmsConnectionListener(jmsConnectionListener);
            amqpManager.setMessageListener(messageListener);
            amqpManager.setQueue(queue);
            amqpManager.setPassword(getPassword(timeStamp));
            amqpManager.setUsername(getUserName(timeStamp,consumerGroupIds[i]));
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

    private String getPassword(long timeStamp) throws Exception {
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
//        Arrays.asList(amqpManagers).forEach(System.out::println);
        Arrays.asList(amqpManagers).forEach(AmqpManager::close);
    }
}
