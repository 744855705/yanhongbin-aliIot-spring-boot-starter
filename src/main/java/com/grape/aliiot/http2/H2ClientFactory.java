package com.grape.aliiot.http2;

import com.aliyun.openservices.iot.api.Profile;
import com.aliyun.openservices.iot.api.message.MessageClientFactory;
import com.aliyun.openservices.iot.api.message.api.MessageClient;
import com.aliyun.openservices.iot.api.message.callback.ConnectionCallback;
import com.grape.aliiot.config.AliIotProperties;
import com.grape.aliiot.exception.BeanInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Created with IDEA
 * description: 获取http/2的messageClient
 * @author :YanHongBin
 * @date :Created in 2019/5/7 9:23
 */
@Component
@Scope("singleton")
public class H2ClientFactory {

    private static final Logger log = LoggerFactory.getLogger(H2ClientFactory.class);

    @Resource(type = AliIotProperties.class)
    private AliIotProperties aliIotProperties;

    /**
     * endPoint:  https://${uid}.iot-as-http2.${region}.aliyuncs.com
     * mns endPoint http://${uid}.mns.${region}.aliyuncs.com
     */
    private String endPoint;

    @PostConstruct
    public void init() throws Exception{
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
