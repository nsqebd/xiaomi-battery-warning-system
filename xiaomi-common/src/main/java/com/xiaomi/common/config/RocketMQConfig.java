package com.xiaomi.common.config;

import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ配置类
 */
@Configuration
public class RocketMQConfig {

    // RocketMQ相关配置，由各服务自行配置

    /**
     * MQ主题常量
     */
    public static final class Topics {
        public static final String SIGNAL_WARNING_TOPIC = "signal_warning_topic";
        public static final String WARNING_RESULT_TOPIC = "warning_result_topic";
    }

    /**
     * MQ标签常量
     */
    public static final class Tags {
        public static final String SIGNAL_REPORT = "signal_report";
        public static final String WARNING_GENERATE = "warning_generate";
    }
}