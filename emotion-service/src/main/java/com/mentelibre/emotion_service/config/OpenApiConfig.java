package com.mentelibre.emotion_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
            .info(new Info()
                .title("Mente Libre Emotion Service API")
                .version("1.0.0")
                .description("Microservicio para el registro, análisis y resumen de emociones de los usuarios.")
            );
    }
}
