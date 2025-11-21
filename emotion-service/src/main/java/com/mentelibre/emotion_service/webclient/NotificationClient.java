package com.mentelibre.emotion_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class NotificationClient {

    private final WebClient webClient;

    public NotificationClient(@Value("${notification.service.url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    // Obtener todas las reglas de notificación
    public List<Object> getAllRules(String token) {
        try {
            return webClient.get()
                    .uri("/api/rules")
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToFlux(Object.class) // usamos Object porque no traemos modelo
                    .collectList()
                    .block();
        } catch (Exception e) {
            return List.of();
        }
    }

    // Obtener una regla específica por ID
    public Object getRuleById(Long id, String token) {
        try {
            return webClient.get()
                    .uri("/api/rules/{id}", id)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }

    // Crear una regla de notificación
    public Object createRule(Object rule, String token) {
        try {
            return webClient.post()
                    .uri("/api/rules")
                    .header("Authorization", token)
                    .bodyValue(rule)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }

    // Actualizar una regla existente
    public Object updateRule(Long id, Object rule, String token) {
        try {
            return webClient.put()
                    .uri("/api/rules/{id}", id)
                    .header("Authorization", token)
                    .bodyValue(rule)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }

    // Activar o desactivar regla
    public Object setRuleActiveStatus(Long id, boolean active, String token) {
        try {
            return webClient.patch()
                    .uri(uriBuilder -> uriBuilder.path("/api/rules/{id}/active")
                            .queryParam("active", active)
                            .build(id))
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }

    // Eliminar regla
    public boolean deleteRule(Long id, String token) {
        try {
            webClient.delete()
                    .uri("/api/rules/{id}", id)
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
