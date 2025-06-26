package com.xiaomi.warning;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 预警服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.xiaomi.warning", "com.xiaomi.common"})
@MapperScan("com.xiaomi.warning.mapper")
@EnableDubbo
public class WarningApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarningApplication.class, args);
    }
}