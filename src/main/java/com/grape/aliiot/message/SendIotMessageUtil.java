package com.grape.aliiot.message;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.iot.model.v20180120.*;

import com.grape.aliiot.config.AliIotProperties;
import com.grape.aliiot.exception.BeanInitException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IDEA
 * description: 发送命令到阿里云IOT平台
 * @author :YanHongBin
 * @date :Created in 2019/5/11 13:49
 */
@Component
@Scope("singleton")
public class SendIotMessageUtil {

    @Resource(type = AliIotProperties.class)
    private AliIotProperties aliIotProperties;

    @Resource(type = AcsClientFactory.class)
    private AcsClientFactory acsClientFactory;


    /**
     * 发送request消息
     * @param request
     * @return
     */
    private <T extends AcsResponse> T sendRequest(AcsRequest<T> request) {
        T response = null;
        try {
            DefaultAcsClient acsClient = acsClientFactory.getAcsClient();
            response = acsClient.getAcsResponse(request);
        } catch (Exception e) {
            System.out.println("执行失败：e:" + e.getMessage());
        }
        return response;
    }

    /**
     * pub消息
     * @param productKey pk
     * @param topic      消息标题
     * @param msg        消息内容
     */
    public boolean pub(String productKey, String topic, String msg){
        PubRequest request = new PubRequest();
        request.setProductKey(productKey);
        request.setMessageContent(Base64.encodeBase64String(msg.getBytes()));
        request.setTopicFullName(topic);
        request.setQos(1);
        PubResponse response =  sendRequest(request);
        System.out.println("发送命令:"+msg);
        if (response != null && response.getSuccess() != false) {
            System.out.println("发送消息成功！messageId：" + response.getMessageId());
            return true;
        } else {
            System.out.println("发送消息失败！requestId:" + response.getRequestId() + "原因：" + response.getErrorMessage());
            return false;
        }
    }

    /**
     * 发送广播消息
     *
     * @param topic 广播标题 /broadcast/${pk}/+
     * @param msg   消息内容
     */
    public void pubBroadcast(String productKey, String topic, String msg) {
        PubBroadcastRequest request = new PubBroadcastRequest();
        request.setProductKey(productKey);
        request.setTopicFullName(topic);
        System.out.println("发送的命令" + msg);
        request.setMessageContent(Base64.encodeBase64String(msg.getBytes()));
        PubBroadcastResponse response = sendRequest(request);
        if (response != null && response.getSuccess() != false) {
            System.out.println("发送消息成功！");
        } else {
            System.out.println("发送消息失败！requestId:" + response.getRequestId() + "原因：" + response.getErrorMessage());
        }
    }


    /**
     * 创建产品
     * @param productName 充电桩id
     * @param productDesc 产品描述 可空
     * @return 产品的PK
     */
    public String createProduct(String productName, String productDesc) {
        CreateProductRequest request = new CreateProductRequest();
        request.setProductName(productName);
        request.setDescription(productDesc);
        CreateProductResponse response = sendRequest(request);
        if (response != null && response.getSuccess()) {
            System.out.println("创建产品成功！productKey:" + response.getProductKey());
            return response.getProductKey();
        } else {
            System.out.println("创建产品失败！requestId:" + response.getRequestId() + "原因:" + response.getErrorMessage());
        }
        return null;
    }

    /**
     * 向阿里iot平台注册设备
     *
     * @param deviceName 充电桩id
     * @return 设备名称
     */
    private RegisterDeviceResponse registDevice(String productKey,String deviceName) {
        RegisterDeviceRequest request = new RegisterDeviceRequest();
        request.setProductKey(productKey);
        request.setDeviceName(deviceName);
        RegisterDeviceResponse response = sendRequest(request);
        if (response != null && response.getSuccess()) {
            System.out.println("创建设备成功！deviceName:" + response.getData().getDeviceName() + ",deviceSecret:" + response.getData().getDeviceSecret());
            return response;
        } else {
            System.out.println(response == null ? ("未获取到RegistDeviceResponse") : ("创建设备失败！requestId:" + response.getRequestId() + "原因：" + response.getErrorMessage()));
            return null;
        }
    }

    /**
     * 查询产品下所有的消息标题
     * @return 直接返回acsResponse,可以用acsResponse.getData()方法获取到所有的消息标题
     */
    public QueryProductTopicResponse queryProductTopic(String productKey){
        QueryProductTopicRequest request = new QueryProductTopicRequest();
        request.setProductKey(productKey);
        QueryProductTopicResponse acsResponse = sendRequest(request);
        List<QueryProductTopicResponse.ProductTopicInfo> data = acsResponse.getData();
        return acsResponse;
    }

    /**
     * 创建消息标题
     * @param name 标题名 格式为 /productKey/${deviceName}/${name}
     */
    public void createProductTopic(String name,String productKey){
        CreateProductTopicRequest createProductTopicRequest = new CreateProductTopicRequest();
        createProductTopicRequest.setProductKey(productKey);
        createProductTopicRequest.setTopicShortName(name);
        createProductTopicRequest.setOperation("ALL");
        CreateProductTopicResponse acsResponse = sendRequest(createProductTopicRequest);
        Boolean success = acsResponse.getSuccess();
        System.out.println(success);
        String errorMessage = acsResponse.getErrorMessage();
        System.out.println(errorMessage);
    }

    /**
     * 查询指定产品下的所有设备列表。
     *
     * @param ProductKey         产品名称  必须
     * @param PageSize            设备命名  非必须
     * @param CurrentPage            设备命名  非必须
     * @return 产品创建信息
     */
    public List<QueryDeviceResponse.DeviceInfo> queryDevice(String ProductKey, Integer PageSize, Integer CurrentPage) {
        QueryDeviceResponse response =null;
        QueryDeviceRequest request = new QueryDeviceRequest();
        request.setProductKey(ProductKey);
        request.setCurrentPage(CurrentPage);
        request.setPageSize(PageSize);
        try {
            response = sendRequest(request);

            if (response.getSuccess() != null && response.getSuccess()) {
                System.out.println("产品下设备列表查询成功");
                List<QueryDeviceResponse.DeviceInfo> data = response.getData();
                for (QueryDeviceResponse.DeviceInfo device: data) {
                    System.out.println(device.getDeviceStatus());
                }
            } else {
                System.out.println("产品下设备列表查询失败");
                System.out.println(JSON.toJSONString(response));
            }
            return response.getData();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("产品下设备列表查询失败！" + JSON.toJSONString(response.getData()));
        }
        return null;
    }

}
