package com.mentelibre.virtualpet_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.mentelibre.virtualpet_service.dto.AuthValidationResponse;

@Component
public class AuthClient {

    private final WebClient webClient;

    public AuthClient(@Value("${auth.service.url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public AuthValidationResponse validateToken(String token) {
        return webClient.get()
            .uri("/auth/validate")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .bodyToMono(AuthValidationResponse.class)
            .block();
    }
}