package com.mentelibre.admin_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.mentelibre.admin_service.dto.JwtValidationResponse;

@Component
public class AuthClient {

    private final WebClient webClient;

    public AuthClient(@Value("${auth.service.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public JwtValidationResponse validateToken(String token) {

        return webClient.get()
                .uri("/api/v1/auth/validate")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(JwtValidationResponse.class)
                .block();
    }
}
