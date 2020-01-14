package com.grape.aliiot.controller;

import com.grape.aliiot.message.SendIotMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IDEA
 * description:
 *
 * @author YanHongBin
 * @date Created in 2020/1/14 14:56
 */
@RestController
public class TestController {


    @Autowired
    private SendIotMessageUtil sendIotMessageUtil;

    @RequestMapping("/test")
    public void test(){
        String deviceName = "TestDevice";
        String productKey = "a1skKPGCozo";
        String message = "TestMessage";
        String topic = "/" + productKey + "/" + deviceName + "/user/get";
        sendIotMessageUtil.pub(productKey, topic, message);
    }



}
