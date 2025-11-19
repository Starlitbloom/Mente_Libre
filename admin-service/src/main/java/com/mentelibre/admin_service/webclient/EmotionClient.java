package com.mentelibre.admin_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class EmotionClient {

    private final WebClient webClient;

    public EmotionClient(@Value("${emotion.service.url}") String emotionUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(emotionUrl)
                .build();
    }

    public List<Object> getAllEmotions(String token) {
        return webClient.get()
                .uri("/api/v1/emotions")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .block();
    }
}
