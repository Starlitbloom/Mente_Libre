package com.mentelibre.evaluation_service.dto;

import com.mentelibre.evaluation_service.model.GratitudeEntry;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Respuesta para enviar entradas de gratitud al cliente.
 */
@Data
@Builder
public class GratitudeEntryResponse {

    private Long id;
    private String userId;
    private LocalDate date;
    private String moodLabel;       // nombre del enum
    private String title;
    private String text;
    private Long dailyEvaluationId; // puede ser null
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GratitudeEntryResponse fromEntity(GratitudeEntry entity) {
        return GratitudeEntryResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .date(entity.getDate())
                .moodLabel(entity.getMoodLabel() != null ? entity.getMoodLabel().name() : null)
                .title(entity.getTitle())
                .text(entity.getText())
                .dailyEvaluationId(
                        entity.getDailyEvaluation() != null ? entity.getDailyEvaluation().getId() : null
                )
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
