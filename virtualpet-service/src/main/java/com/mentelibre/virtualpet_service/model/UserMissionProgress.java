package com.mentelibre.virtualpet_service.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(
    name = "user_mission_progress",
    uniqueConstraints = {
        // Evita que un usuario complete la misma misión muchas veces el mismo día (para misiones diarias)
        @UniqueConstraint(columnNames = {"user_id", "mission_id", "completion_date"})
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Registro de misiones de mascota completadas por cada usuario")
public class UserMissionProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del progreso de misión", example = "1")
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    @Schema(description = "ID del usuario", example = "5")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    @Schema(description = "Misión completada por el usuario")
    private Mission mission;

    @NotNull
    @Column(name = "completion_date", nullable = false)
    @Schema(description = "Fecha de completación (solo fecha, sin hora)")
    private LocalDate completionDate;

    @Schema(description = "Indica si la misión fue completada", example = "true")
    @Column(nullable = false)
    private boolean completed = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}