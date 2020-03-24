package com.yanhongbin.aliiot.config;

import lombok.Data;

/**
 * Created with IDEA
 * description: 消费组id,和该消费组的消息处理器BeanId封装
 *
 * @author YanHongBin
 * @date Created in 2020/3/9 10:17
 */
@Data
public class ConsumerGroupMessageConfig{
    /**
     * 消费组id
     */
    private String consumerGroupId;

    /**
     * 当前消费组消息处理器beanId
     */
    private String messageProcessorBeanId;
}
