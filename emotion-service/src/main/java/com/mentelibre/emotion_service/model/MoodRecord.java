// src/main/java/com/mentelibre/emotion_service/model/MoodRecord.java
package com.mentelibre.emotion_service.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Registro de ánimo de un usuario (equivalente a MoodEntry en Android).
 */
@Entity
@Table(name = "mood_records")
public class MoodRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;              // id del usuario

    @Column(nullable = false)
    private LocalDate recordDate;     // fecha del registro

    @Column(nullable = false, length = 30)
    private String emotionLabel;      // Feliz / Triste / etc.

    @Column(nullable = false)
    private Integer emotionScore;     // 100 / 85 / 75 / ...

    @Column(columnDefinition = "TEXT")
    private String context;           // opcional (puede ser null)

    public MoodRecord() {
    }

    public MoodRecord(String userId,
                      LocalDate recordDate,
                      String emotionLabel,
                      Integer emotionScore,
                      String context) {
        this.userId = userId;
        this.recordDate = recordDate;
        this.emotionLabel = emotionLabel;
        this.emotionScore = emotionScore;
        this.context = context;
    }

    // ===== Getters =====
    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public LocalDate getRecordDate() { return recordDate; }
    public String getEmotionLabel() { return emotionLabel; }
    public Integer getEmotionScore() { return emotionScore; }
    public String getContext() { return context; }

    // ===== Setters =====
    public void setUserId(String userId) { this.userId = userId; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }
    public void setEmotionLabel(String emotionLabel) { this.emotionLabel = emotionLabel; }
    public void setEmotionScore(Integer emotionScore) { this.emotionScore = emotionScore; }
    public void setContext(String context) { this.context = context; }
}
