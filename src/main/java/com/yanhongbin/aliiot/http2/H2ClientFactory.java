package com.yanhongbin.aliiot.http2;

import com.aliyun.openservices.iot.api.Profile;
import com.aliyun.openservices.iot.api.message.MessageClientFactory;
import com.aliyun.openservices.iot.api.message.api.MessageClient;
import com.aliyun.openservices.iot.api.message.callback.ConnectionCallback;
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

/**
 * Created with IDEA
 * description: 获取http/2的messageClient
 *
 * @author :YanHongBin
 * @date :Created in 2019/5/7 9:23
 *
 * @deprecated :阿里官方不在使用http2的服务端订阅
 */
@Component
@Scope("singleton")
@Deprecated
public class H2ClientFactory {

    private static final Logger log = LoggerFactory.getLogger(H2ClientFactory.class);

    @Resource(type = AliIotProperties.class)
    private AliIotProperties aliIotProperties;

    @Resource(type = ConnectConfig.class)
    private ConnectConfig connectConfig;
    /**
     * endPoint:  https://${uid}.iot-as-http2.${region}.aliyuncs.com
     * mns endPoint http://${uid}.mns.${region}.aliyuncs.com
     */
    private String endPoint;

    @PostConstruct
    public void init() throws Exception{
        SubscribeSwitch subscribeSwitch = connectConfig.getSubscribeSwitch();
        if (subscribeSwitch == null || subscribeSwitch == SubscribeSwitch.OFF) {
            // 关闭服务端订阅功能,不需要初始化
            return;
        }
        ConnectSetting type = connectConfig.getType();
        if (type != ConnectSetting.HTTP2) {
            // 不是HTTP2,不初始化
            return;
        }
        if (aliIotProperties != null) {
            endPoint = "https://" + aliIotProperties.getUid() + ".iot-as-http2." + aliIotProperties.getRegionId().toString() + ".aliyuncs.com";
        }else{
            throw new BeanInitException("aliIotProperties 注入失败");
        }
        // Bean初始化之后初始化MessageClient
        profile = Profile.getAccessKeyProfile(endPoint, aliIotProperties.getRegionId().toString(), aliIotProperties.getAccessKeyId(), aliIotProperties.getAccessKeySecret());
        client = MessageClientFactory.messageClient(profile);
        ConnectionCallback connectionCallback = new ConnectionCallback() {
            @Override
            public void onConnectionLost() {
                log.info("=============# mqtt is lost #=============");
            }
            @Override
            public void onConnected(boolean isReconnected) {
                log.info("==========# mqtt is connected #==========");
            }
        };
        client.setConnectionCallback(connectionCallback);
    }

    /**
     * 连接配置
     */
    private Profile profile;
    /**
     * 构造客户端
     */
    private MessageClient client ;


    public MessageClient getMessageClient(){
        return client;
    }

}
