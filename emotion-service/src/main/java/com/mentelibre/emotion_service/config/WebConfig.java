package com.mentelibre.emotion_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web básica.
 * - Habilita CORS para que tu app Android / front puedan llamar al microservicio.
 */
@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        // Rutas de tu API (ajusta si usas otro prefijo)
                        .addMapping("/api/**")
                        // Orígenes permitidos (ajusta según necesites)
                        .allowedOrigins(
                                "http://localhost:8080",   // otro backend / gateway
                                "http://localhost:3000",   // front web
                                "http://10.0.2.2:8080"     // emulador Android -> PC
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
