package com.xiaomi.signal;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 信号服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.xiaomi.signal", "com.xiaomi.common"})
@MapperScan("com.xiaomi.signal.mapper")
@EnableDubbo
@EnableScheduling
public class SignalApplication {

    public static void main(String[] args) {
        SpringApplication.run(SignalApplication.class, args);
    }
}