package com.yanhongbin.aliiot.config;

import com.yanhongbin.aliiot.config.enumerate.ConnectSetting;
import com.yanhongbin.aliiot.config.enumerate.RegionEnum;
import com.yanhongbin.aliiot.config.enumerate.SubscribeSwitch;
import com.yanhongbin.aliiot.exception.PropertiesNotFoundException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * Created with IDEA
 * description: 阿里云IOT 配置,由配置文件导入
 * @author YanHongBin
 * @date Created in 2020/1/13 10:29
 */
@ConfigurationProperties("spring.yanhongbin.ali.iot.config")
@Component
@Scope("singleton")
@Data
public class AliIotProperties implements InitializingBean {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Resource(type = ConnectConfig.class)
    private ConnectConfig connectConfig;


    /**
     * 阿里云账户accessKeyId
     */
    private String accessKeyId;

    /**
     * 阿里云账户accessKeySecret
     */
    private String accessKeySecret;

    /**
     * 地区id cn-shanghai(华东2)、ap-southeast-1（新加坡） 、us-west-1（美西）
     */
    private RegionEnum regionId;

    /**
     * 阿里云用户id
     */
    private String uid;

    /**
     * 阿里云产品名
     */
    private String productCode;

    /**
     * domain
     */
    private String domain;

    /**
     * 产品Key
     */
    private String[] productKey;


    /**
     * 消息处理器beanId配置
     */
    private HashMap<String, String> messageProcessorBeanId;


    @Override
    public void afterPropertiesSet() throws Exception {

        // 指定为Iot套件
        productCode = "Iot";
        if (accessKeyId == null) {
            throw new PropertiesNotFoundException("无法找到 accessKeyId 配置信息");
        }
        if (accessKeySecret == null) {
            throw new PropertiesNotFoundException("无法找到 accessKeySecret 配置信息");
        }
        if (regionId == null) {
            throw new PropertiesNotFoundException("无法找到 regionId 配置信息");
        }
        if (uid == null) {
            throw new PropertiesNotFoundException("无法找到 uid 配置信息");
        }
        SubscribeSwitch subscribeSwitch = connectConfig.getSubscribeSwitch();
        if(subscribeSwitch == null || subscribeSwitch == SubscribeSwitch.OFF){
            // 默认不开启服务端订阅
            // 不开启则不需要检验productKey等参数
            return;
        }

        if (connectConfig.getType() == null || connectConfig.getType() == ConnectSetting.HTTP2) {
            // 使用Http2长连接, 不需要productKey和consumerGroupId
        }else if( connectConfig.getType() == ConnectSetting.MNS){
            if (productKey == null) {
                throw new PropertiesNotFoundException("无法找到 productKey 配置信息");
            }
        }
        domain = "iot." + regionId + ".aliyuncs.com";
        log.info("iot配置信息:{}",this);

    }
}
