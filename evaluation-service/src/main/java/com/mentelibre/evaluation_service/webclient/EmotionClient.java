package com.mentelibre.evaluation_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class EmotionClient {

    private final WebClient webClient;

    public EmotionClient(@Value("${emotion.service.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                                  .baseUrl(baseUrl)
                                  .build();
    }

    // Validar si un usuario existe en Emotion Service
    public boolean existeUsuario(Long userId, String token) {
        try {
            webClient.get()
                .uri("/api/v1/emotions/user/{id}", userId)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class) // No necesitamos model específico
                .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Obtener emociones de un usuario
    public Object obtenerEmotionsPorUsuario(Long userId, String token) {
        return webClient.get()
                .uri("/api/v1/emotions/user/{id}", userId)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    // Crear una nueva emoción
    public Object crearEmotion(Object emotion, String token) {
        return webClient.post()
                .uri("/api/v1/emotions")
                .header("Authorization", token)
                .bodyValue(emotion)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    // Obtener resumen semanal de emociones
    public Object obtenerResumenSemanal(Long userId, String token) {
        return webClient.get()
                .uri("/api/v1/emotions/summary/week/{id}", userId)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    // ---------------- NotificationClient endpoints ----------------
    public Object listarReglasNotificacion(String token) {
        return webClient.get()
                .uri("/api/v1/emotions/notifications/rules")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public Object obtenerReglaNotificacion(Long id, String token) {
        return webClient.get()
                .uri("/api/v1/emotions/notifications/rules/{id}", id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public Object crearReglaNotificacion(Object regla, String token) {
        return webClient.post()
                .uri("/api/v1/emotions/notifications/rules")
                .header("Authorization", token)
                .bodyValue(regla)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public Object actualizarReglaNotificacion(Long id, Object regla, String token) {
        return webClient.put()
                .uri("/api/v1/emotions/notifications/rules/{id}", id)
                .header("Authorization", token)
                .bodyValue(regla)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public Object activarDesactivarRegla(Long id, boolean active, String token) {
        return webClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/emotions/notifications/rules/{id}/active")
                        .queryParam("active", active)
                        .build(id))
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public boolean eliminarRegla(Long id, String token) {
        try {
            webClient.delete()
                .uri("/api/v1/emotions/notifications/rules/{id}", id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
