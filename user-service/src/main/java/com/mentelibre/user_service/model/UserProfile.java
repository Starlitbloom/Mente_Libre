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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
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

    @Column(nullable = false, length = 30)
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 4, max = 30, message = "El nombre debe tener entre 4 y 30 caracteres")
    @Schema(description = "Nombre del usuario", example = "Maria")
    private String nombre;

    @Column(nullable = false, length = 30)
    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(min = 4, max = 30, message = "El apellido debe tener entre 4 y 30 caracteres")
    @Schema(description = "Apellido del usuario", example = "Calfileo")
    private String apellido;

    @Size(max = 255, message = "La URL de la foto no puede exceder los 255 caracteres")
    @Pattern(regexp = "^(https?|ftp)://[^\s/$.?#].[^\s]*$", message = "La URL de la foto debe ser válida") // Validación para URL de foto de perfil
    @Schema(description = "URL de la foto de perfil", example = "https://example.com/foto.jpg")
    private String fotoPerfil;

    @Size(max = 255, message = "La biografía no puede exceder los 255 caracteres")
    @Schema(description = "Breve biografía del usuario", example = "Soy una estudiante de informática.")
    private String biografia;

    @Column(nullable = false, unique = true, length = 12)
    @NotBlank(message = "El número de teléfono no puede estar vacío")
    @Pattern(regexp = "\\+?\\d{9,12}", message = "El número de teléfono debe contener entre 9 y 12 dígitos")
    @Schema(description = "Número de teléfono del usuario", example = "+56987654321")
    private String telefono; // Número de teléfono del usuario, debe ser único y tener entre 9 y 12 dígitos

    @Past(message = "La fecha de nacimiento debe ser una fecha pasada") // Validación para que la fecha de nacimiento sea en el pasado
    @NotNull(message = "La fecha de nacimiento no puede estar vacía")
    @Schema(description = "Fecha de nacimiento del usuario", example = "2006-01-15")
    private LocalDate fechaNacimiento; // Fecha de nacimiento del usuario, debe ser una fecha pasada

    @Column(nullable = false, length = 50) // Longitud máxima de la dirección
    @Size(max = 50, message = "La dirección no puede exceder los 50 caracteres")
    @NotBlank(message = "La dirección no puede estar vacía") // Validación para que la dirección no esté vacía
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
}
