package com.mentelibre.notification_service.model;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una regla de notificación configurada por un administrador.
 * 
 * Cada regla define:
 * - Tipo de notificación (RECORDATORIO, LOGRO, ALERTA)
 * - Frecuencia (DIARIA, SEMANAL, INMEDIATA)
 * - Estado activo/inactivo
 * - Fechas automáticas de creación y actualización
 */
@Data
@Entity
@Table(name = "notification_rules")
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Regla de notificación configurada por un administrador")
public class NotificationRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID autoincremental
    @Schema(description = "Identificador único de la regla", example = "1")
    private Long id;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "El tipo de notificación no puede estar vacío")
    @Schema(description = "Tipo de notificación", example = "RECORDATORIO")
    private String type; // RECORDATORIO, LOGRO, ALERTA

    @Column(nullable = false, length = 50)
    @NotBlank(message = "La frecuencia no puede estar vacía")
    @Schema(description = "Frecuencia de la notificación", example = "DIARIA")
    private String frequency; // DIARIA, SEMANAL, INMEDIATA

    @Column(nullable = false)
    @Schema(description = "Indica si la regla está activa", example = "true")
    private boolean active = true; // True si la regla está habilitada

    @CreationTimestamp
    @Column(updatable = false)
    @Schema(description = "Fecha y hora de creación de la regla")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Schema(description = "Fecha y hora de la última actualización de la regla")
    private LocalDateTime updatedAt;

   
}
