package com.grape.aliiot.config;

import com.grape.aliiot.config.enumerate.ConnectSetting;
import com.grape.aliiot.config.enumerate.SignMethod;
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
     * 消费组id,使用AMQP服务端订阅时需要配置
     */
    private String[] consumerGroupId;

    /**
     * 签名方法：支持hmacmd5，hmacsha1和hmacsha256
     */
    private SignMethod signMethod;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (connectConfig.getType() == null|| connectConfig.getType() == ConnectSetting.AMQP) {
            // 没有配置的情况下默认使用AMQP
            if (consumerGroupId == null || consumerGroupId.length == 0) {
                throw new PropertiesNotFoundException("无法找到 consumerGroupId 配置信息");
            }
        }
        if (signMethod == null) {
            // 默认使用 hmacsha1 生成签名
            signMethod = SignMethod.HMACSHA1;
        }
        log.info("AmqpProperties;{}",this);
    }
}
