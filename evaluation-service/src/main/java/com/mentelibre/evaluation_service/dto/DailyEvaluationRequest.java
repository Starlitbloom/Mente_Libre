package com.mentelibre.evaluation_service.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * Request para crear/actualizar una evaluación diaria (Bitácora).
 * 
 * OJO: por ahora el userId va por query/header.
 */
@Data
public class DailyEvaluationRequest {

    private LocalDate date;      // yyyy-MM-dd
    private String moodLabel;    // "FELIZ", "TRISTE", etc (nombre del enum)
    private Integer globalScore; // 0–100 (opcional)
    private String reflection;   // texto opcional
}
