package com.mjuletter.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("2024 명지편지 프로젝트 API")
                        .description("명지대학교 학우들을 위한 롤링페이퍼 서비스입니다.")
                        .version("1.0.0"));
    }
}