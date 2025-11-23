// src/main/java/com/mentelibre/emotion_service/dto/RegisterMoodRequest.java
package com.mentelibre.emotion_service.dto;

import java.time.LocalDate;

public class RegisterMoodRequest {

    private Long userId;           // id usuario (del auth-service)
    private String emotionLabel;   // Feliz / Triste / etc.
    private String context;        // opcional, puede ser null
    private LocalDate recordDate;  // fecha del registro

    public RegisterMoodRequest() {
    }

    // ===== Getters =====
    public Long getUserId() { return userId; }
    public String getEmotionLabel() { return emotionLabel; }
    public String getContext() { return context; }
    public LocalDate getRecordDate() { return recordDate; }

    // ===== Setters =====
    public void setUserId(Long userId) { this.userId = userId; }
    public void setEmotionLabel(String emotionLabel) { this.emotionLabel = emotionLabel; }
    public void setContext(String context) { this.context = context; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }
}
