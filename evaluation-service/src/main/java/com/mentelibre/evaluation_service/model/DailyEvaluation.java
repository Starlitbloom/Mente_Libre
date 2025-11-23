package com.mentelibre.evaluation_service.model;

import com.mentelibre.evaluation_service.model.MoodLabel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_evaluations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private MoodLabel mainMood;

    private Integer globalScore;

    @Column(length = 2000)
    private String reflection;

    private LocalDateTime createdAt;

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
