// src/main/java/com/mentelibre/emotion_service/dto/MoodRecordDto.java
package com.mentelibre.emotion_service.dto;

import java.time.LocalDate;

public class MoodRecordDto {

    private LocalDate recordDate;
    private String emotionLabel;
    private Integer emotionScore;
    private String context;

    public MoodRecordDto() {
    }

    public MoodRecordDto(LocalDate recordDate,
                         String emotionLabel,
                         Integer emotionScore,
                         String context) {
        this.recordDate = recordDate;
        this.emotionLabel = emotionLabel;
        this.emotionScore = emotionScore;
        this.context = context;
    }

    public LocalDate getRecordDate() { return recordDate; }
    public String getEmotionLabel() { return emotionLabel; }
    public Integer getEmotionScore() { return emotionScore; }
    public String getContext() { return context; }

    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }
    public void setEmotionLabel(String emotionLabel) { this.emotionLabel = emotionLabel; }
    public void setEmotionScore(Integer emotionScore) { this.emotionScore = emotionScore; }
    public void setContext(String context) { this.context = context; }
}
