package com.grape.aliiot.config;

import com.grape.aliiot.config.enumerate.ConnectSetting;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IDEA
 * description:
 *
 * @author YanHongBin
 * @date Created in 2020/1/13 17:19
 */
@ConfigurationProperties("spring.grape.ali.iot.connect")
@Component
@Scope("singleton")
@Data
public class ConnectConfig implements InitializingBean {

    private Logger log = LoggerFactory.getLogger(getClass());
    /**
     * 连接类型
     */
    private ConnectSetting type;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("type:{}",type);
        if (type == null) {
            // 默认使用AMQP连接,需要在阿里云控制台开启消息推送服务
            type = ConnectSetting.AMQP;
        }
        log.info("type:{}",type);
    }
}
