package com.yanhongbin.aliiot.message;

import com.yanhongbin.aliiot.message.service.MessageProcessor;
import com.yanhongbin.aliiot.message.service.impl.DefaultMessageProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created with IDEA
 * description :
 *
 * @author ：yanhongbin
 * @date : Created in 2020/4/9 12:51 上午
 */
@Configuration
public class MessageProcessConfig {

    /**
     * 配置默认的消息处理器
     * @return DefaultMessageProcessor
     */
    @Bean
    @ConditionalOnMissingBean(MessageProcessor.class)
    public MessageProcessor messageProcessor() {
        return new DefaultMessageProcessor();
    }

    /**
     * 配置处理消息线程池默认的 ThreadFactory
     * @return DefaultThreadFactory
     */
    @Bean
    @ConditionalOnMissingBean(ThreadFactory.class)
    public ThreadFactory threadFactory(){
        return Executors.defaultThreadFactory();
    }

    /**
     * 配置默认的任务拒绝策略
     * @return AbortPolicy
     */
    @Bean
    @ConditionalOnMissingBean(RejectedExecutionHandler.class)
    public RejectedExecutionHandler rejectedExecutionHandler() {
        return new ThreadPoolExecutor.AbortPolicy();
    }


}
