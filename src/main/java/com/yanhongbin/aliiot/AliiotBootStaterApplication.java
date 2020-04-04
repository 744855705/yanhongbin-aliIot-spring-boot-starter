package com.yanhongbin.aliiot;

import com.yanhongbin.aliiot.utils.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author spring
 */
@SpringBootApplication
public class AliiotBootStaterApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(AliiotBootStaterApplication.class, args);
        // 防止初始化失败，可不加
        SpringUtil.setApplicationContextIfNull(run);
    }

}
