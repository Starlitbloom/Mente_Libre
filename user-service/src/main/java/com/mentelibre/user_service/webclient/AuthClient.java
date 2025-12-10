package com.mentelibre.user_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.mentelibre.user_service.config.AuthValidationResponse;
import com.mentelibre.user_service.dto.AuthUserDTO;

@Component
public class AuthClient {

    private final WebClient webClient;

    public AuthClient(@Value("${auth.service.url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    // Valida token y obtiene userId (lo que usa JwtRequestFilter)
    public AuthValidationResponse validateToken(String token) {
        return webClient.get()
                .uri("/auth/validate")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(AuthValidationResponse.class)
                .block();
    }

    // Obtiene datos del usuario autenticado para crear perfil
    public AuthUserDTO getAuthenticatedUser(String token) {
        return webClient.get()
                .uri("/auth/me")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(AuthUserDTO.class)
                .block();
    }

}
