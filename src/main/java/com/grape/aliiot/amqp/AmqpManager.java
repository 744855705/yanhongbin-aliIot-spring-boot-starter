package com.grape.aliiot.amqp;

import com.grape.aliiot.amqp.util.ClientIdUtil;
import com.grape.aliiot.config.AliIotProperties;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.qpid.jms.JmsConnection;
import org.apache.qpid.jms.JmsConnectionListener;

import javax.annotation.Resource;
import javax.jms.*;
import java.io.IOException;

/**
 * Created with IDEA
 * description: Amqp 启动类
 *
 * @author YanHongBin
 * @date Created in 2020/1/14 13:27
 */
@Data
public class AmqpManager extends Thread {


    /**
     * 连接工厂
     */
    private ConnectionFactory cf;
    /**
     * 拼接用户名
     */
    private String username;

    /**
     * 签名生成的密码
     */
    private String password;

    private Destination queue;
    /**
     * 连接监听器
     */
    private JmsConnectionListener jmsConnectionListener;
    /**
     * 消息处理监听器
     */
    private MessageListener messageListener;

    /**
     * 连接对象
     */
    private Connection connection;
    @SneakyThrows
    @Override
    public void run() {
        connection = cf.createConnection(username, password);
        ((JmsConnection) connection).addConnectionListener(jmsConnectionListener);
        // Create Session
        // Session.CLIENT_ACKNOWLEDGE: 收到消息后，需要手动调用message.acknowledge()
        // Session.AUTO_ACKNOWLEDGE: SDK自动ACK（推荐）
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        connection.start();
        // Create Receiver Link
        MessageConsumer consumer = session.createConsumer(queue);
        consumer.setMessageListener(messageListener);
    }

    public void close() {
        System.out.println("==================================================close==================================================");
        try {
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
