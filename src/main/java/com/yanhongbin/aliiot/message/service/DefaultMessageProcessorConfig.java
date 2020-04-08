package com.yanhongbin.aliiot.message.service;

import com.yanhongbin.aliiot.message.service.impl.DefaultMessageProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IDEA
 * description :
 *
 * @author ：yanhongbin
 * @date : Created in 2020/4/9 12:51 上午
 */
@Configuration
public class DefaultMessageProcessorConfig {

    @Bean
    @ConditionalOnMissingBean
    public MessageProcessor messageProcessor() {
        return new DefaultMessageProcessor();
    }
}
