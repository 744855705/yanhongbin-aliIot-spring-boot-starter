package com.grape.aliiot.starter;

import com.grape.aliiot.amqp.AmqpStarter;
import com.grape.aliiot.config.AliIotProperties;
import com.grape.aliiot.config.AmqpProperties;
import com.grape.aliiot.config.ConnectConfig;
import com.grape.aliiot.config.enumerate.SubscribeSwitch;
import com.grape.aliiot.http2.MqttManager;
import com.grape.aliiot.mns.MnsStarter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * Created with IDEA
 * description: 服务端订阅启动入口类,配置使用服务端订阅则从此处启动
 *
 * @author YanHongBin
 * @date Created in 2020/1/13 17:44
 */
@ComponentScan("com.grape.*")
@Scope("singleton")
@Component
@EnableConfigurationProperties({AliIotProperties.class, AmqpProperties.class,ConnectConfig.class})
@SuppressWarnings("all")
public class ConnectStarter implements ApplicationRunner {

    @Resource(type = AliIotProperties.class)
    private AliIotProperties aliIotProperties;

    @Resource(type = ConnectConfig.class)
    private ConnectConfig connectConfig;

    @Resource(type = MnsStarter.class)
    private MnsStarter mnsStarter;

    @Resource(type = MqttManager.class)
    private MqttManager mqttManager;

    @Resource(type = AmqpStarter.class)
    private AmqpStarter amqpStarter;

    @Override
//    @SuppressWarnings("all")
    public void run(ApplicationArguments args) throws Exception {
        SubscribeSwitch subscribeSwitch = connectConfig.getSubscribeSwitch();
        if(subscribeSwitch == SubscribeSwitch.ON){
            switch (connectConfig.getType()) {
                case MNS:
                    mnsStarter.start();
                    break;
                case HTTP2:
                    mqttManager.start();
                    break;
                case AMQP:
                    amqpStarter.startAmqp();
            }
        }
    }

    @PreDestroy
    public void destroy(){
        if (connectConfig.getSubscribeSwitch() == SubscribeSwitch.ON) {
            // 如果配置了开启服务端订阅,开启连接
            switch (connectConfig.getType()) {
                case MNS:
                    mnsStarter.destroyMnsService();
                    break;
                case HTTP2:
                    mqttManager.destroyBean();
                    break;
                case AMQP:
                    amqpStarter.destroyBean();
            }
        }
    }
}
