package com.yanhongbin.aliiot.starter;

import com.yanhongbin.aliiot.amqp.AmqpStarter;
import com.yanhongbin.aliiot.config.AliIotProperties;
import com.yanhongbin.aliiot.config.AmqpProperties;
import com.yanhongbin.aliiot.config.ConnectConfig;
import com.yanhongbin.aliiot.config.enumerate.SubscribeSwitch;
import com.yanhongbin.aliiot.http2.MqttManager;
import com.yanhongbin.aliiot.mns.MnsStarter;
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
@ComponentScan("com.yanhongbin.*")
@Scope("singleton")
@Component
@EnableConfigurationProperties({AliIotProperties.class, AmqpProperties.class,ConnectConfig.class})
public class ConnectStarter implements ApplicationRunner {

    @Resource(type = ConnectConfig.class)
    private ConnectConfig connectConfig;

    @Resource(type = MnsStarter.class)
    private MnsStarter mnsStarter;

    @Resource(type = MqttManager.class)
    private MqttManager mqttManager;

    @Resource(type = AmqpStarter.class)
    private AmqpStarter amqpStarter;

    /**
     * 项目启动后执行该方法,判断是否开启服务端订阅,若开启,按照配置启动服务端订阅
     * {@link ApplicationRunner#run(ApplicationArguments)}
     * @param args ApplicationArguments
     * @throws Exception
     */
    @Override
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
                    break;
            }
        }
    }

    /**
     * 销毁Bean时调用,正确的结束服务端订阅消息线程
     */
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
                    break;
            }
        }
    }
}
