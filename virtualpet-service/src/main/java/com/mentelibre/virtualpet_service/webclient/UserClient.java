package com.mentelibre.virtualpet_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.mentelibre.virtualpet_service.dto.UserProfileResponseDto;

@Component
public class UserClient {

    private final WebClient webClient;

    public UserClient(@Value("${user.service.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    // Obtener el perfil del usuario (requiere TOKEN)
    public UserProfileResponseDto getMyProfile(String token) {
        return webClient.get()
                .uri("/profile/me")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(UserProfileResponseDto.class)
                .block();
    }
}