package com.grape.aliiot;

import com.grape.aliiot.utils.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AliiotBootStaterApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(AliiotBootStaterApplication.class, args);
        SpringUtil.setApplicationContextIfNull(run);
    }

}
