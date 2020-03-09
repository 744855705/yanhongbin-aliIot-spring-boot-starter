package com.grape.aliiot.config;

import lombok.Data;

/**
 * Created with IDEA
 * description:
 *
 * @author YanHongBin
 * @date Created in 2020/3/9 10:17
 */
@Data
public class ConsumerGroupMessageConfig{
    private String consumerGroupId;
    private String messageProcessorBeanId;
}
