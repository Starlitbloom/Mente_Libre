package com.mentelibre.emotion_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.mentelibre.emotion_service.dto.AuthValidationResponse;

@Component
public class AuthClient {

    private final WebClient webClient;

    public AuthClient(@Value("${auth.service.url}") String authServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(authServiceUrl)
                .build();
    }

    // Valida el token enviándolo al Auth Service
    public AuthValidationResponse validateToken(String token) {

        return webClient.get()
                .uri("/auth/validate")
                .header("Authorization", token)  // debe ir "Bearer ...."
                .retrieve()
                .bodyToMono(AuthValidationResponse.class)
                .block();   // síncrono para usar dentro del filtro
    }
}