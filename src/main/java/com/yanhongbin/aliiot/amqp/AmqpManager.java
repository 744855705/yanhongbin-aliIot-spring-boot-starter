package com.yanhongbin.aliiot.amqp;

import com.yanhongbin.aliiot.message.service.MessageProcessor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.qpid.jms.JmsConnection;
import org.apache.qpid.jms.JmsConnectionListener;

import javax.jms.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IDEA
 * description: Amqp 启动类
 *
 * @author YanHongBin
 * @date Created in 2020/1/14 13:27
 */
@Data
@Slf4j
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

    /**
     * 当前连接的消息处理器
     */
    private MessageProcessor messageProcessor;

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
        log.info("close AMQP at:{}",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        try {
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
