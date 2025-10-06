package com.mentelibre.goals_service.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Entidad que representa el progreso diario o periódico de una meta.
 * 
 * Ejemplo: el usuario marca como "hecho" un día de seguimiento de emociones.
 */
@Data
@Entity
@Table(name = "goal_progress")
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Registro de progreso diario o periódico de una meta")

public class GoalProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del registro de progreso", example = "10")
    private Long id;

    @Schema(description = "Fecha en la que se realizó el seguimiento", example = "2025-10-06")
    private LocalDate date;

    @Schema(description = "Indica si el progreso del día fue cumplido", example = "true")
    private boolean done;

    @ManyToOne
    @JoinColumn(name = "goal_id", nullable = false)
    @JsonIgnoreProperties({"progressList", "achievements"})
    @Schema(description = "Meta a la que pertenece este registro de progreso")
    private Goal goal;

    @CreationTimestamp
    @Column(updatable = false)
    @Schema(description = "Fecha y hora en que se registró el progreso")
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Schema(description = "Fecha y hora en que se actualizó el progreso")
    private LocalDateTime actualizadoEn;
}


