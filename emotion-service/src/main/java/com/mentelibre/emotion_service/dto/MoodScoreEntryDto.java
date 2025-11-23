package com.mentelibre.emotion_service.dto;


import java.time.LocalDate;

/**
 * Puntaje diario calculado por el backend según moodType.
 */
public class MoodScoreEntryDto {

    private LocalDate date;
    private String moodType;
    private int score; // 0..100

    // ===== Constructors =====

    public MoodScoreEntryDto() {}

    public MoodScoreEntryDto(LocalDate date, String moodType, int score) {
        this.date = date;
        this.moodType = moodType;
        this.score = score;
    }

    // ===== Getters / Setters =====

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getMoodType() { return moodType; }
    public void setMoodType(String moodType) { this.moodType = moodType; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
