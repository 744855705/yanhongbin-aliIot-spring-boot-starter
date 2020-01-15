package com.grape.aliiot.amqp.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created with IDEA
 * description:: 签名计算工具类
 *
 * @author YanHongBin
 * @date Created in 2020/1/14 11:46
 */
public class SignUtil {

    /**
     * password签名计算方法，请参见上一篇文档：AMQP客户端接入说明。
     */
    public static String doSign(String toSignString, String secret, String signMethod) throws Exception {
        SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), signMethod);
        Mac mac = Mac.getInstance(signMethod);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(toSignString.getBytes());
        return Base64.encodeBase64String(rawHmac);
    }

}
