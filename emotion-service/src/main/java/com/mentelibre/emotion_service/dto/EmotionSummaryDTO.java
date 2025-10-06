package com.mentelibre.emotion_service.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Resumen de emociones agrupadas por período de tiempo")
public class EmotionSummaryDTO {

    @Schema(description = "Fecha del registro agrupado", example = "2025-10-01")
    private LocalDate fecha;

    @Schema(description = "Tipo de emoción predominante", example = "Feliz")
    private String tipoPredominante;

    @Schema(description = "Promedio de intensidad en el período", example = "7.3")
    private Double promedioIntensidad;
}
