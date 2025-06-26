package com.xiaomi.rule.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("小米电池预警系统 - 规则服务API")
                        .description("预警规则管理和规则引擎服务的API文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("小米团队")
                                .email("team@xiaomi.com")));
    }
}