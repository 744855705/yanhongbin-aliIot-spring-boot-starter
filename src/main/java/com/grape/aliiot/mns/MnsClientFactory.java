package com.grape.aliiot.mns;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.MNSClient;
import com.grape.aliiot.config.AliIotProperties;
import com.grape.aliiot.config.ConnectConfig;
import com.grape.aliiot.config.enumerate.ConnectSetting;
import com.grape.aliiot.config.enumerate.SubscribeSwitch;
import com.grape.aliiot.exception.BeanInitException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Created with IDEA
 * description:
 * @author :YanHongBin
 * @date :Created in 2019/8/6 10:43
 */
@Component
@Scope("singleton")
public class MnsClientFactory {

    @Resource(type = AliIotProperties.class)
    private AliIotProperties aliIotProperties;

    @Resource(type = ConnectConfig.class)
    private ConnectConfig connectConfig;
    /**
     * CloudAccount 需要使用单例模式
     */
    private CloudAccount[] account;


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
        String endpoint;
        if (aliIotProperties != null) {
            endpoint = "https://"+aliIotProperties.getUid()+".mns."+aliIotProperties.getRegionId().toString()+".aliyuncs.com/";
        }else{
            throw new BeanInitException("aliIotProperties 注入失败");
        }
        account = new CloudAccount[aliIotProperties.getProductKey().length];
        // 启动时初始化
        for (int i = 0; i < account.length; i++) {
            account[i] = new CloudAccount(aliIotProperties.getAccessKeyId(), aliIotProperties.getAccessKeySecret(), endpoint);
        }
    }
    /**
     * CloudAccount[]
     * @return CloudAccount
     */
    public CloudAccount[] getCloudAccount(){
        return account;
    }

    /**
     * 获取MNSClient[]
     * @return MNSClient
     */
    public MNSClient[] getMNSClient() {
        // MNSClient 已经由 account.getMNSClient()实现单例
        CloudAccount[] cloudAccount = getCloudAccount();
        MNSClient[] mnsClients = new MNSClient[cloudAccount.length];
        for (int i = 0; i < cloudAccount.length; i++) {
            mnsClients[i] = cloudAccount[i].getMNSClient();
        }
        return mnsClients;
    }



}
