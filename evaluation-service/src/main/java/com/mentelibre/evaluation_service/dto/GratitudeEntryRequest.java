package com.mentelibre.evaluation_service.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * Request para crear una entrada de gratitud.
 */
@Data
public class GratitudeEntryRequest {

    private LocalDate date;        // fecha del día (yyyy-MM-dd), opcional
    private String moodLabel;      // opcional, nombre del enum ("FELIZ", "TRISTE", etc.)
    private String title;          // opcional
    private String text;           // obligatorio (contenido principal)
    private Long dailyEvaluationId; // opcional: vincular a una DailyEvaluation concreta
}
