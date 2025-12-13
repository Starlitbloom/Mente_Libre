package com.mentelibre.virtualpet_service.config;

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
                .title("Mente Libre - Pet Service API")
                .version("1.0.0")
                .description("Microservicio para la gestión de mascota virtual y progreso del usuario.")
            );
    }
}