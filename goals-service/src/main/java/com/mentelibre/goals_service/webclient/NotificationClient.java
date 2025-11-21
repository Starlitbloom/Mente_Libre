package com.mentelibre.goals_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class NotificationClient {

    private final WebClient webClient;

    public NotificationClient(@Value("${notification.service.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Obtiene todas las notificaciones de un usuario específico.
     */
    public List<Object> getNotificationsByUser(Long userId) {
        return webClient.get()
                .uri("/api/notifications/user/{userId}", userId)
                .retrieve()
                .bodyToFlux(Object.class) // usamos Object si no quieres importar Notification
                .collectList()
                .block();
    }

    /**
     * Marca una notificación como enviada.
     */
    public boolean markNotificationAsSent(Long notificationId) {
        try {
            webClient.patch()
                    .uri("/api/notifications/{id}/sent", notificationId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Crea una nueva notificación.
     */
    public boolean createNotification(Object notification) {
        try {
            webClient.post()
                    .uri("/api/notifications")
                    .bodyValue(notification)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
