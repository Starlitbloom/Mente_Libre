package com.mentelibre.admin_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class UserClient {

    private final WebClient webClient;

    public UserClient(@Value("${user.service.url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    // Obtener todos los perfiles (requiere rol ADMIN)
    public List<Object> getAllProfiles(String token) {
        return webClient.get()
                .uri("/api/v1/usuario_perfil/perfiles")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(Object.class) // Aquí podrías usar un DTO si quieres
                .collectList()
                .block();
    }

    // Obtener perfil por userId
    public Object getProfileByUserId(Long userId, String token) {
        return webClient.get()
                .uri("/api/v1/usuario_perfil/perfiles/user/{userId}", userId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    // Crear un perfil de usuario
    public Object createProfile(Object profile, String token) {
        return webClient.post()
                .uri("/api/v1/usuario_perfil/perfiles")
                .header("Authorization", "Bearer " + token)
                .bodyValue(profile)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    // Actualizar un perfil
    public Object updateProfile(Long id, Object profile, String token) {
        return webClient.put()
                .uri("/api/v1/usuario_perfil/perfiles/{id}", id)
                .header("Authorization", "Bearer " + token)
                .bodyValue(profile)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    // Eliminar perfil
    public boolean deleteProfile(Long id, String token) {
        try {
            webClient.delete()
                    .uri("/api/v1/usuario_perfil/perfiles/{id}", id)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
