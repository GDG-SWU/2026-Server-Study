package com.example.server_study_2026.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration; // 이 줄로 올바르게 수정
import org.springframework.context.annotation.Bean;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library API")
                        .description("Server Study Library API Documentation")
                        .version("v1.0.0"));
    }
}