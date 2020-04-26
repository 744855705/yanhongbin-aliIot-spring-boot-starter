package com.yanhongbin.aliiot.message.threadpool;

import com.yanhongbin.aliiot.config.ThreadPoolConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.*;

/**
 * Created with IDEA
 * description : 简单的 ThreadPoolExecutor 代理
 *
 * @author ：yanhongbin
 * @date : Created in 2020/4/25 5:28 下午
 */
@Component
@Scope("singleton")
public class ThreadPoolProxy implements InitializingBean, DisposableBean {


    private static ThreadPoolExecutor threadPoolExecutor;

    private static ArrayBlockingQueue<Runnable> arrayBlockingQueue;

    @Resource
    private ThreadPoolConfig threadPoolConfig;

    @Resource(type = ThreadFactory.class)
    private ThreadFactory threadFactory;

    @Resource(type = RejectedExecutionHandler.class)
    private RejectedExecutionHandler rejectedExecutionHandler;

    /**
     * 提交任务
     * @param r runnable
     */
    public static void execute(Runnable r) {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.execute(r);
        }
    }

    /**
     * 关闭线程池
     */
    public static void shutdown() {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdown();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (threadPoolConfig.getThreadPoolSwitch()) {
            // bean加载完成后，配置使用线程池
            // 初始化任务队列
            arrayBlockingQueue = new ArrayBlockingQueue<Runnable>(threadPoolConfig.getWorkQueueSize());
            // 初始化线程池
            threadPoolExecutor = new ThreadPoolExecutor(
                    threadPoolConfig.getCorePoolSize(),
                    threadPoolConfig.getMaximumPoolSize(),
                    threadPoolConfig.getKeepAliveTime(),
                    threadPoolConfig.getUnit(),
                    arrayBlockingQueue,
                    threadFactory,
                    rejectedExecutionHandler);
        }
    }

    /**
     * 销毁bean的时候关闭线程池
     * @throws Exception null
     */
    @Override
    public void destroy() throws Exception {
        shutdown();
    }
}
