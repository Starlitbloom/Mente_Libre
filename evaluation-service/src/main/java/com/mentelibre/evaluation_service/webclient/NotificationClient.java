package com.mentelibre.evaluation_service.webclient;

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

    // ===================== Notificaciones =====================
    public List<Object> getAllNotifications(String token) {
        return webClient.get()
                .uri("/api/notifications")
                .header("Authorization", token)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .block();
    }

    public List<Object> getNotificationsByUser(Long userId, String token) {
        return webClient.get()
                .uri("/api/notifications/user/{userId}", userId)
                .header("Authorization", token)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .block();
    }

    public Object getNotificationById(Long id, String token) {
        return webClient.get()
                .uri("/api/notifications/{id}", id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public Object createNotification(Object notification, String token) {
        return webClient.post()
                .uri("/api/notifications")
                .header("Authorization", token)
                .bodyValue(notification)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public Object updateNotification(Long id, Object notification, String token) {
        return webClient.put()
                .uri("/api/notifications/{id}", id)
                .header("Authorization", token)
                .bodyValue(notification)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public Object markAsSent(Long id, String token) {
        return webClient.patch()
                .uri("/api/notifications/{id}/sent", id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public boolean deleteNotification(Long id, String token) {
        try {
            webClient.delete()
                    .uri("/api/notifications/{id}", id)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ===================== Reglas de Notificación =====================
    public List<Object> getAllRules(String token) {
        return webClient.get()
                .uri("/api/rules")
                .header("Authorization", token)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .block();
    }

    public Object getRuleById(Long id, String token) {
        return webClient.get()
                .uri("/api/rules/{id}", id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public Object createRule(Object rule, String token) {
        return webClient.post()
                .uri("/api/rules")
                .header("Authorization", token)
                .bodyValue(rule)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public Object updateRule(Long id, Object rule, String token) {
        return webClient.put()
                .uri("/api/rules/{id}", id)
                .header("Authorization", token)
                .bodyValue(rule)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public Object setRuleActiveStatus(Long id, boolean active, String token) {
        return webClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/api/rules/{id}/active")
                                             .queryParam("active", active)
                                             .build(id))
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

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
