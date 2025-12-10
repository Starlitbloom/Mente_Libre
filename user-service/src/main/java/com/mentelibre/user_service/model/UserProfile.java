package com.mentelibre.user_service.model;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "perfiles_usuario")
@Entity
@AllArgsConstructor
@NoArgsConstructor

@Schema(description = "Perfil de usuario con datos personales, contacto y preferencias")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del perfil", example = "1")
    private Long id;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Schema(description = "ID del usuario asociado", example = "1")
    private Long userId;

    @Size(max = 255, message = "La URL de la foto no puede exceder los 255 caracteres")
    @Column(name = "foto_perfil")
    @Schema(description = "URL de la foto de perfil", example = "https://example.com/foto.jpg")
    private String fotoPerfil;

    @Past(message = "La fecha debe ser pasada") // Validación para que la fecha de nacimiento sea en el pasado
    @Schema(description = "Fecha de nacimiento del usuario", example = "2006-01-15")
    private LocalDate fechaNacimiento; // Fecha de nacimiento del usuario, debe ser una fecha pasada

    @Column(length = 50) // Longitud máxima de la dirección
    @Size(max = 50, message = "La dirección no puede exceder los 50 caracteres")
    @Schema(description = "Dirección del usuario", example = "Volcan Villarrica 429")
    private String direccion; // Dirección del usuario

    @NotNull(message = "La preferencia de notificaciones no puede estar vacía")
    @Schema(description = "Preferencia de notificaciones (true para activadas)", example = "true")
    private Boolean notificaciones; // Preferencia de notificaciones del usuario, no puede ser nula

    @ManyToOne
    @JoinColumn(name = "genero_id", nullable = false)
    @NotNull(message = "El género es obligatorio")
    @Schema(description = "Género del usuario", example = "FEMENINO")
    private Genero genero;

    @Size(max = 100, message = "El objetivo no puede exceder los 100 caracteres.")
    @Schema(
        description = "Objetivo de salud diario seleccionado por el usuario",
        example = "Reducir estrés y organizar prioridades"
    )
    private String objetivo;

    @Size(max = 50, message = "El tema no puede exceder los 50 caracteres.")
    @Schema(
        description = "Tema visual de la aplicación elegido por el usuario",
        example = "Rosado"
    )
    private String tema;

    @NotNull(message = "Debe indicar si la huella está activada o no.")
    @Schema(
        description = "Define si el usuario activó el inicio de sesión por huella",
        example = "true"
    )
    private Boolean huellaActiva;
}
