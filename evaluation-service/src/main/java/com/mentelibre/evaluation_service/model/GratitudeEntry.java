package com.mentelibre.evaluation_service.model;

import com.mentelibre.evaluation_service.model.MoodLabel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gratitude_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GratitudeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Id del usuario que escribió la entrada
    @Column(name = "user_id", nullable = false)
    private String userId;

    // Fecha del día asociado a esta entrada
    @Column(nullable = false)
    private LocalDate date;

    // Estado de ánimo asociado (puede ser null)
    @Enumerated(EnumType.STRING)
    @Column(name = "mood_label", length = 30)
    private MoodLabel moodLabel;

    // Título opcional
    @Column(length = 200)
    private String title;

    // Texto principal
    @Column(nullable = false, length = 4000)
    private String text;

    // Relación opcional con la evaluación diaria
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_evaluation_id")
    private DailyEvaluation dailyEvaluation;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
