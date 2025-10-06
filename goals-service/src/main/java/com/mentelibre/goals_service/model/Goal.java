package com.mentelibre.goals_service.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Entidad que representa una meta saludable definida por un usuario.
 * 
 * Ejemplo: "Registrar emociones durante 7 días seguidos".
 * 
 * Cada meta puede tener múltiples registros de progreso diario y generar logros.
 */

@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@Entity
@Table(name = "goals") // Nombre de la tabla en la base de datos
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Meta saludable definida por un usuario para alcanzar objetivos personales")

public class Goal {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la meta", example = "1")
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El título de la meta no puede estar vacío")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    @Schema(description = "Título o nombre de la meta", example = "Registrar emociones diariamente")
    private String title;

    @Column(length = 255)
    @Schema(description = "Descripción breve de la meta", example = "Registrar emociones durante 7 días seguidos")
    private String description;

    @Schema(description = "Fecha de inicio de la meta", example = "2025-10-05")
    private LocalDate startDate;

    @Schema(description = "Fecha límite o fin de la meta", example = "2025-10-12")
    private LocalDate endDate;

    @Schema(description = "Indica si la meta ha sido completada", example = "false")
    private boolean completed = false;

    @Column(nullable = false)
    @Schema(description = "Identificador del usuario (obtenido desde AuthService)", example = "5")
    private Long userId;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("goal")
    @Schema(description = "Lista de registros de progreso asociados a la meta")
    private List<GoalProgress> progressList;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("goal")
    @Schema(description = "Lista de logros obtenidos relacionados con la meta")
    private List<Achievement> achievements;

    @CreationTimestamp
    @Column(updatable = false)
    @Schema(description = "Fecha y hora en que la meta fue creada automáticamente por el sistema")
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Schema(description = "Fecha y hora en que la meta fue actualizada por última vez")
    private LocalDateTime actualizadoEn;
    
}
