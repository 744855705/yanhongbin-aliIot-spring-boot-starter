package com.yanhongbin.aliiot.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created with IDEA
 * description :
 *
 * @author ：yanhongbin
 * @date : Created in 2020/4/25 5:30 下午
 */
@Component
@ConfigurationProperties("thread.pool")
@Data
@Slf4j
public class ThreadPoolConfig implements InitializingBean {



    /**
     * 核心线程数
     */
    private Integer corePoolSize;

    /**
     * 最大线程数
     */
    private Integer maximumPoolSize;

    /**
     * 线程存活时间
     */
    private Long keepAliveTime;

    /**
     * 线程存活时间单位
     */
    private TimeUnit unit;

    /**
     * 任务队列大小
     */
    private Integer workQueueSize;

    /**
     * 是否使用线程池处理消息
     */
    private Boolean threadPoolSwitch;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.threadPoolSwitch == null) {
            // 未配置默认不开启
            threadPoolSwitch = false;
        }
        if (corePoolSize == null) {
            corePoolSize = Runtime.getRuntime().availableProcessors();
        }
        if (maximumPoolSize == null) {
            // 接收并处理消息一般不需要太多计算，假设任务为IO密集型，阻塞系数为 0.5，最大线程数应为 2*可用核心数
            maximumPoolSize = Runtime.getRuntime().availableProcessors() << 1;
        }
        if (workQueueSize == null) {
            // 队列大小默认为可用核心数
            workQueueSize = Runtime.getRuntime().availableProcessors();
        }
        if (keepAliveTime == null) {
            // 线程存活时间默认为0
            keepAliveTime = 0L;
        }
        if (unit == null) {
            // 存活时间单位默认毫秒
            unit = TimeUnit.MILLISECONDS;
        }

        if (!threadPoolSwitch) {
            log.info("消息处理线程池配置信息{}", this);
        }
    }
}
