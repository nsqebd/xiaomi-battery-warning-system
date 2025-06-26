package com.xiaomi.rule;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 规则服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.xiaomi.rule", "com.xiaomi.common"})
@MapperScan("com.xiaomi.rule.mapper")
@EnableDubbo
public class RuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuleApplication.class, args);
    }
}