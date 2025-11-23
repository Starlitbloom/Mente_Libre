package com.mentelibre.emotion_service.dto;


import java.util.List;

/**
 * Resumen completo de puntaje de bienestar mental.
 * Este objeto alimenta totalmente tu pantalla PuntajeScreen.
 */
public class MoodScoreSummaryDto {

    private Long userId;
    private int days;               // ej: 30
    private int overallScore;       // promedio 0..100
    private String healthMessage;   // mensajeSalud(...)
    private List<MoodScoreEntryDto> entries;

    // ===== Constructors =====

    public MoodScoreSummaryDto() {}

    public MoodScoreSummaryDto(
            Long userId,
            int days,
            int overallScore,
            String healthMessage,
            List<MoodScoreEntryDto> entries
    ) {
        this.userId = userId;
        this.days = days;
        this.overallScore = overallScore;
        this.healthMessage = healthMessage;
        this.entries = entries;
    }

    // ===== Getters / Setters =====

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }

    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }

    public String getHealthMessage() { return healthMessage; }
    public void setHealthMessage(String healthMessage) { this.healthMessage = healthMessage; }

    public List<MoodScoreEntryDto> getEntries() { return entries; }
    public void setEntries(List<MoodScoreEntryDto> entries) { this.entries = entries; }
}
