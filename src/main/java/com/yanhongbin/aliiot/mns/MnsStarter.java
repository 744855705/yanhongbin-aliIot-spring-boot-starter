package com.yanhongbin.aliiot.mns;

import com.aliyun.mns.client.MNSClient;
import com.yanhongbin.aliiot.config.AliIotProperties;
import com.yanhongbin.aliiot.config.ConnectConfig;
import com.yanhongbin.aliiot.config.enumerate.ConnectSetting;
import com.yanhongbin.aliiot.config.enumerate.SubscribeSwitch;
import com.yanhongbin.aliiot.exception.BeanInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created with IDEA
 * description: 多个Mns队列监听启动类
 *
 * @author YanHongBin
 * @date Created in 2020/1/14 8:59
 */
@Component
@Scope("singleton")
public class MnsStarter extends Thread{

    private Logger log = LoggerFactory.getLogger(getClass());

    @Resource(type = MnsClientFactory.class)
    private MnsClientFactory mnsClientFactory;

    @Resource(type = AliIotProperties.class)
    private AliIotProperties aliIotProperties;

    @Resource(type = ConnectConfig.class)
    private ConnectConfig connectConfig;
    /**
     * MNSClient 用于开启MNS监听
     */
    private MNSClient[] mnsClients;

    /**
     * 队列名集合
     */
    private String[] queueNames;

    /**
     * 启动线程数组
     */
    private MnsManager[] mnsManagers;

    /**
     * bean销毁标记 volatile 保证线程之间可见
     */
    static volatile boolean flag = true;

    @PostConstruct
    public void init() throws Exception{
        SubscribeSwitch subscribeSwitch = connectConfig.getSubscribeSwitch();
        if (subscribeSwitch == null || subscribeSwitch == SubscribeSwitch.OFF) {
            // 关闭服务端订阅功能,不需要初始化
            return;
        }
        ConnectSetting type = connectConfig.getType();
        if (type != ConnectSetting.MNS) {
            // 不是MNS,不初始化
            return;
        }
        if (mnsClientFactory != null) {
            mnsClients = mnsClientFactory.getMNSClient();
        }else{
            throw new BeanInitException("aliIotProperties 注入失败");
        }
        if (aliIotProperties != null) {
            String[] productKey = aliIotProperties.getProductKey();
            queueNames = new String[productKey.length];
            mnsManagers = new MnsManager[queueNames.length];
            for (int i = 0; i < productKey.length; i++) {
                queueNames[i] = "aliyun-iot-" + productKey[i];
                mnsManagers[i] = new MnsManager(mnsClients[i], queueNames[i], productKey[i]);
                mnsManagers[i].setAliIotProperties(aliIotProperties);
            }
        }

//        log.info("MnsStater初始化完成");
    }

    @Override
    public void run() {
        Arrays.asList(mnsManagers).forEach(Thread::start);
    }


    /**
     * 销毁Bean时调用
     */
    public void destroyMnsService() {
        log.info("close MNS at:{}",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        MnsStarter.flag = false;
    }

}
