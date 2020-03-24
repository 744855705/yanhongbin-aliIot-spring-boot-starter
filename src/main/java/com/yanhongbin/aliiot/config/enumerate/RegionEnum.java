package com.yanhongbin.aliiot.config.enumerate;

/**
 * Created with IDEA
 * description: 阿里云iot地区id
 * @author YanHongBin
 * @date Created in 2020/1/13 11:24
 */
public enum  RegionEnum {

    /**
     * 华东2
     */
    SHANGHAI("cn-shanghai"),
    /**
     * 新加坡
     */
    AP_SOUTHEAST("ap-southeast-1"),
    /**
     * 美西
     */
    US_WEST("us-west-1"),
    ;
    private String code;

    RegionEnum(String code){
        this.code = code;
    }

    /**
     * 重写toString,可以使用枚举直接进行字符串拼接
     * @return code
     */
    @Override
    public String toString() {
        return this.code;
    }

    public String getCode() {
        return this.code;
    }
}
