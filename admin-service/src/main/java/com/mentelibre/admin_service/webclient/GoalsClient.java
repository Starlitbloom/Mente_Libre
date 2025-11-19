package com.mentelibre.admin_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class GoalsClient {

    private final WebClient webClient;

    public GoalsClient(@Value("${goals.service.url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    // ======================= GOALS =======================

    public List<Object> getAllGoals(String token) {
        return webClient.get()
                .uri("/api/goals")
                .header("Authorization", token)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .block();
    }

    public Object getGoalById(Long id, String token) {
        return webClient.get()
                .uri("/api/goals/{id}", id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    // ==================== GOAL PROGRESS ====================

    public List<Object> getAllProgressByGoal(Long goalId, String token) {
        return webClient.get()
                .uri("/api/goals/progress/goal/{goalId}", goalId)
                .header("Authorization", token)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .block();
    }

    public List<Object> getAllProgress(String token) {
        return webClient.get()
                .uri("/api/goals/progress")
                .header("Authorization", token)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .block();
    }

    // ===================== ACHIEVEMENTS =====================

    public List<Object> getAllAchievements(String token) {
        return webClient.get()
                .uri("/api/goals/achievements")
                .header("Authorization", token)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .block();
    }

    public List<Object> getAchievementsByGoal(Long goalId, String token) {
        return webClient.get()
                .uri("/api/goals/achievements/goal/{goalId}", goalId)
                .header("Authorization", token)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .block();
    }
}
