package com.mentelibre.emotion_service.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Table(name = "emociones")
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Registro de emociones de los usuarios, incluyendo tipo, intensidad y fecha.")
public class Emotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del registro de emoción", example = "1")
    private Long id;

    @NotNull(message = "El ID del usuario es obligatorio.")
    @Schema(description = "ID del usuario asociado a la emoción", example = "12")
    private Long userId;

    @NotBlank(message = "El tipo de emoción no puede estar vacío.")
    @Size(max = 30, message = "El tipo de emoción no puede superar los 30 caracteres.")
    @Schema(description = "Tipo de emoción registrada", example = "Felicidad")
    private String tipo;

    @NotNull(message = "La intensidad es obligatoria.")
    @Min(value = 1, message = "La intensidad mínima es 1.")
    @Max(value = 10, message = "La intensidad máxima es 10.")
    @Schema(description = "Nivel de intensidad de la emoción (1 a 10)", example = "7")
    private Integer intensidad;

    @NotNull(message = "La fecha de registro es obligatoria.")
    @PastOrPresent(message = "La fecha no puede ser futura.")
    @Schema(description = "Fecha y hora en que se registró la emoción", example = "2025-10-04T15:30:00")
    private LocalDateTime fechaRegistro;

    @NotBlank(message = "El contexto no puede estar vacío.")
    @Size(max = 50, message = "El contexto no puede exceder los 50 caracteres.")
    @Schema(description = "Contexto en el que se registró la emoción", example = "Estudio")
    private String contexto; // Ej: trabajo, estudio, ocio, relaciones, etc.
}