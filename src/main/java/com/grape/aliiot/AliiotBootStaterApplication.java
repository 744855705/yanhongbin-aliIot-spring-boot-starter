package com.grape.aliiot;

import com.grape.aliiot.config.AliIotProperties;
import com.grape.aliiot.message.SendIotMessageUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties(AliIotProperties.class)
public class AliiotBootStaterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AliiotBootStaterApplication.class, args);
    }

}
