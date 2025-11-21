package com.mentelibre.goals_service.webclient;

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

    /**
     * Verifica si un usuario existe por su ID
     */
    public boolean usuarioExiste(Long userId, String token) {
        try {
            webClient.get()
                    .uri("/api/v1/users/{id}", userId)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida si el usuario tiene permisos para crear goals.
     * Puedes definir en AuthController un endpoint como /api/v1/auth/validate-goal-access
     * que devuelva true/false según el rol del usuario.
     */
    public boolean usuarioPuedeCrearGoal(String token) {
        try {
            return webClient.get()
                    .uri("/api/v1/auth/validate-goal-access")
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (Exception e) {
            return false;
        }
    }

}
