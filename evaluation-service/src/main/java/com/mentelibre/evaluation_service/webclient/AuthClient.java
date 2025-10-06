package com.mentelibre.evaluation_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthClient {
    private final WebClient webClient;

    public AuthClient(@Value("${auth.service.url}") String authServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(authServiceUrl)
                .build();
    }
    public boolean usuarioExiste(Long userId, String token) {
        try {
            webClient.get()
                .uri("/users/{id}", userId)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean usuarioPuedeCrearEvaluacion(String authHeader) {
        try {
            return webClient.get()
                .uri("/auth/validate-course-access/0") // Puedes usar cualquier endpoint que valide el rol
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        } catch (Exception e) {
            return false;
        }
    }

}