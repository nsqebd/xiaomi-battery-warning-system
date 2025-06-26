package com.xiaomi.vehicle;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 车辆服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.xiaomi.vehicle", "com.xiaomi.common"})
@MapperScan("com.xiaomi.vehicle.mapper")
@EnableDubbo
public class VehicleApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleApplication.class, args);
    }
}