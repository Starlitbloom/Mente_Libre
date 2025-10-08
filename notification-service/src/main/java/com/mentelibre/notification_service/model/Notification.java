package com.mentelibre.notification_service.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Entidad que representa una notificación enviada a un estudiante.
 * 
 * Cada notificación tiene:
 * - Tipo (ej: RECORDATORIO, LOGRO, ALERTA)
 * - Mensaje
 * - Usuario receptor (userId)
 * - Estado de envío (sent)
 * - Fechas automáticas de creación y actualización
 */
@Data // Genera getters, setters, equals, hashCode y toString automáticamente
@Entity // Marca esta clase como entidad JPA
@Table(name = "notifications") // Nombre de la tabla en la base de datos
@NoArgsConstructor // Genera constructor vacío
@AllArgsConstructor // Genera constructor con todos los campos
@Schema(description = "Notificación enviada a un estudiante")
public class Notification {
     @Id // Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincremental
    @Schema(description = "Identificador único de la notificación", example = "1")
    private Long id;

    @Column(nullable = false, length = 50) // No puede ser nulo, max 50 caracteres
    @NotBlank(message = "El tipo de notificación no puede estar vacío") // Validación
    @Schema(description = "Tipo de notificación", example = "RECORDATORIO")
    private String type; // RECORDATORIO, LOGRO, ALERTA

    @Column(nullable = false, length = 500)
    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 500, message = "El mensaje no puede superar 500 caracteres")
    @Schema(description = "Contenido de la notificación", example = "Recuerda registrar tus emociones de hoy")
    private String message;

    @Column(nullable = false)
    @Schema(description = "ID del estudiante receptor", example = "5")
    private Long userId; // ID del estudiante que recibe la notificación

    @Column(nullable = false)
    @Schema(description = "Indica si la notificación ya fue enviada", example = "false")
    private boolean sent = false; // True si ya fue enviada

    @CreationTimestamp // Fecha de creación automática
    @Column(updatable = false)
    @Schema(description = "Fecha y hora de creación de la notificación")
    private LocalDateTime createdAt;

    @UpdateTimestamp // Fecha de actualización automática
    @Schema(description = "Fecha y hora de la última actualización de la notificación")
    private LocalDateTime updatedAt;
   
}

