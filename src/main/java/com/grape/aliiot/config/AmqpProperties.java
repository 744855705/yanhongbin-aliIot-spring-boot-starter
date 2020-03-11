package com.grape.aliiot.config;

import com.grape.aliiot.config.enumerate.ConnectSetting;
import com.grape.aliiot.config.enumerate.SignMethod;
import com.grape.aliiot.config.enumerate.SubscribeSwitch;
import com.grape.aliiot.exception.PropertiesNotFoundException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IDEA
 * description: AMQP部分配置信息,使用AMQP服务端订阅时生效
 *
 * @author YanHongBin
 * @date Created in 2020/1/14 11:51
 */
@ConfigurationProperties("spring.grape.ali.iot.amqp")
@Component
@Scope("singleton")
@Data
public class AmqpProperties implements InitializingBean {

    private Logger log = LoggerFactory.getLogger(getClass());


    @Resource(type = ConnectConfig.class)
    private ConnectConfig connectConfig;

    /**
     * 消费组id,和该消费组的消息处理器BeanId使用AMQP服务端订阅时需要配置
     */
    private ConsumerGroupMessageConfig[] consumerGroupMessageConfig;

    /**
     * 签名方法：支持hmacmd5，hmacsha1和hmacsha256
     */
    private SignMethod signMethod;

    @Override
    public void afterPropertiesSet() throws Exception {
        SubscribeSwitch subscribeSwitch = connectConfig.getSubscribeSwitch();
        if (subscribeSwitch == null || subscribeSwitch == SubscribeSwitch.OFF) {
            // 默认不开启服务端订阅
            // 不开启订阅不需要配置AMQP相关参数
            return;
        }

        if (connectConfig.getType() == null|| connectConfig.getType() == ConnectSetting.AMQP) {
            // 没有配置的情况下默认使用AMQP
            if (consumerGroupMessageConfig == null || consumerGroupMessageConfig.length == 0) {
                throw new PropertiesNotFoundException("无法找到 spring.grape.ali.iot.amqp.consumerGroupMessageConfig 配置信息");
            }
        }
        if (signMethod == null) {
            // 默认使用 hmacsha1 生成签名
            signMethod = SignMethod.HMACSHA1;
        }
        log.info("AmqpProperties;{}",this);
    }


}

