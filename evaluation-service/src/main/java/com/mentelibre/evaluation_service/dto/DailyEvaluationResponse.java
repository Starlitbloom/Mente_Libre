package com.mentelibre.evaluation_service.dto;

import com.mentelibre.evaluation_service.model.DailyEvaluation;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Respuesta para enviar la evaluación diaria al cliente (Android, Postman, etc.).
 */
@Data
@Builder
public class DailyEvaluationResponse {

    private Long id;
    private String userId;
    private LocalDate date;
    private String moodLabel;     // nombre del enum: "FELIZ", "TRISTE", etc.
    private Integer globalScore;
    private String reflection;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DailyEvaluationResponse fromEntity(DailyEvaluation entity) {
        return DailyEvaluationResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .date(entity.getDate())
                .moodLabel(entity.getMainMood() != null ? entity.getMainMood().name() : null)
                .globalScore(entity.getGlobalScore())
                .reflection(entity.getReflection())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
