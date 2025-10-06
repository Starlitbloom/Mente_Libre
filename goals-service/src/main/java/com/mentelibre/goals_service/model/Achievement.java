package com.mentelibre.goals_service.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Entidad que representa los logros obtenidos por un usuario al cumplir una meta o alcanzar un hito.
 * 
 * Ejemplo: "Completó la meta de registrar emociones 7 días seguidos".
 */
@Data
@Entity
@Table(name = "achievements")
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Logro alcanzado por el usuario al cumplir una meta o hito importante")

public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del logro", example = "3")
    private Long id;

    @NotBlank(message = "El nombre del logro no puede estar vacío")
    @Schema(description = "Nombre del logro obtenido", example = "7 días consecutivos completados")
    private String name;

    @Schema(description = "Descripción adicional del logro", example = "El usuario completó el seguimiento diario por una semana.")
    private String description;

    @Schema(description = "Fecha en que se alcanzó el logro", example = "2025-10-12")
    private LocalDate achievedDate;

    @ManyToOne
    @JoinColumn(name = "goal_id", nullable = false)
    @JsonIgnoreProperties({"achievements", "progressList"})
    @Schema(description = "Meta asociada a este logro")
    private Goal goal;

    @CreationTimestamp
    @Column(updatable = false)
    @Schema(description = "Fecha y hora en que se registró el logro")
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Schema(description = "Fecha y hora en que se actualizó el logro")
    private LocalDateTime actualizadoEn;

}
