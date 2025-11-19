package com.mentelibre.admin_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class EvaluationClient {

    private final WebClient webClient;

    public EvaluationClient(@Value("${evaluation.service.url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    // Obtener todas las evaluaciones
    public List<Object> getAllEvaluations(String token) {
        return webClient.get()
                .uri("/api/v1/evaluations")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .block();
    }

    // Obtener preguntas de una evaluación
    public List<Object> getQuestionsByEvaluationId(Long evaluationId, String token) {
        return webClient.get()
                .uri("/api/v1/evaluations/{id}/questions", evaluationId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .block();
    }

    // Obtener resultados por usuario
    public List<Object> getResultsByUserId(Long userId, String token) {
        return webClient.get()
                .uri("/api/v1/evaluations/results/user/{userId}", userId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .block();
    }
}
