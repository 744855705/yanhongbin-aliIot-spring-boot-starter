package com.grape.aliiot.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IDEA
 * description: 获取Spring Bean工具类
 *
 * @author YanHongBin
 * @date Created in 2020/3/9 10:20
 */
@Component
@Scope("singleton")
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 初始化 applicationContext
     * @param applicationContext applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }

    /**
     * 根据Bean 的名字获取Bean
     *
     * @param name bean的名字
     * @return Object类型的bean
     */
    public static Object getBean(String name) {
        if (applicationContext != null) {
            return applicationContext.getBean(name);
        }
        throw new RuntimeException("applicationContext注入失败,不能使用getBean方法");
    }

//    public <T> T getBean(String name,Class<T> clazz) {
//        return (T)applicationContext.getBean(name);
//    }

    /**
     * 根据类型获取Bean
     *
     * @param clazz bean的class
     * @param <T>   声明的类型
     * @return <T>类型的bean
     */
    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext != null) {
            return applicationContext.getBean(clazz);
        }
        throw new RuntimeException("applicationContext注入失败,不能使用getBean方法");
    }

    public static void setApplicationContextIfNull(ApplicationContext applicationContext) {
        if (SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }

    }
}
