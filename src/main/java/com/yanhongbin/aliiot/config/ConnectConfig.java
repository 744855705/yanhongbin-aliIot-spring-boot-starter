package com.yanhongbin.aliiot.config;

import com.yanhongbin.aliiot.config.enumerate.ConnectSetting;
import com.yanhongbin.aliiot.config.enumerate.SubscribeSwitch;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IDEA
 * description: 服务端订阅配置,是否开启,使用哪种类型订阅
 *
 * @author YanHongBin
 * @date Created in 2020/1/13 17:19
 */
@ConfigurationProperties("spring.yanhongbin.ali.iot.connect")
@Component
@Scope("singleton")
@Data
public class ConnectConfig implements InitializingBean {

    private Logger log = LoggerFactory.getLogger(getClass());
    /**
     * 连接类型
     */
    private ConnectSetting type;

    /**
     * 是否开启服务端订阅
     */
    private SubscribeSwitch subscribeSwitch;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (subscribeSwitch == null) {
            // 默认不开启服务端订阅
            subscribeSwitch = SubscribeSwitch.OFF;
        }
        if (type == null) {
            // 默认使用AMQP连接,需要在阿里云控制台开启消息推送服务
            type = ConnectSetting.AMQP;
        }
        log.info("type:{}",type);
    }
}
