package com.mentelibre.virtualpet_service.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "missions")
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Misión o desafío que puede completar el estudiante para ganar puntos")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la misión", example = "1")
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 80)
    @Schema(description = "Título de la misión", example = "Registrar tu emoción de hoy")
    private String title;

    @NotBlank
    @Column(nullable = false, length = 300)
    @Schema(description = "Descripción de la misión")
    private String description;

    @NotBlank
    @Column(nullable = false, length = 30)
    @Schema(description = "Tipo de misión", example = "DIARIA")
    private String type; 
    // DIARIA, SEMANAL, LOGRO, ESPECIAL...

    @NotNull
    @Column(nullable = false)
    @Schema(description = "Puntos que entrega la misión", example = "10")
    private Integer pointsReward;

    @Schema(description = "Indica si la misión está activa")
    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}