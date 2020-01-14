package com.grape.aliiot.mns;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;


/**
 * Created with IDEA
 * description: 用于存储Mns消息服务中获取的消息, 内含一个com.alibaba.fastjson.JSONObject对象
 * @author :YanHongBin
 * @date :Created in 2019/8/6 11:04
 */
public class MnsMessage {
    private final JSONObject messageBody;
    /**
     * 将返回的messageBody 转为JSON放入对象中
     * @param text messageBody
     * @return MnsMessage 包含messageBody
     */
    public static MnsMessage parseMnsMessage(String text) {
        return new MnsMessage(JSON.parseObject(text));
    }

    /**
     * 私有构造函数,只能通过parseMnsMessage方式构造对象
     * @param messageBody messageBody
     */
    private MnsMessage(JSONObject messageBody) {
        this.messageBody = messageBody;
    }

    public String getPayload(){
        return new String(Base64.decodeBase64(String.valueOf(this.messageBody.get("payload"))), StandardCharsets.UTF_8);
    }

    public String getMessagetype(){
        return String.valueOf(this.messageBody.get("messagetype"));
    }

    public String getTopic(){
        return String.valueOf(this.messageBody.get("topic"));
    }
    public String getMessageid(){
        return String.valueOf(this.messageBody.get("messageid"));
    }

    public String getTimestamp(){
        return String.valueOf(this.messageBody.get("timestamp"));
    }

}
