package com.mentelibre.user_service.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos requeridos para crear un perfil de usuario")
public class CreateUserProfileRequestDto {

    @NotNull
    @Schema(description = "ID del usuario (proveniente de Auth-Service)", example = "12")
    private Long userId;

    @Size(max = 255)
    @Schema(description = "URL de la foto de perfil (opcional)", example = "https://url.com/foto.png")
    private String fotoPerfil; // OPCIONAL

    @Past
    @Schema(description = "Fecha de nacimiento (opcional)", example = "2006-03-11")
    private LocalDate fechaNacimiento; // OPCIONAL

    @Size(max = 50)
    @Schema(description = "Dirección del usuario (opcional)", example = "Av. Las Torres 1324")
    private String direccion; // OPCIONAL

    @Schema(description = "Preferencias de notificaciones", example = "true")
    private Boolean notificaciones = true; // DEFAULT true

    @NotNull
    @Schema(description = "ID del género asociado", example = "2")
    private Long generoId; // ESTE SÍ ES REQUERIDO

    @Size(max = 100)
    @Schema(description = "Objetivo de salud del usuario (opcional)", example = "Reducir estrés")
    private String objetivo; // OPCIONAL

    @Size(max = 50)
    @Schema(description = "Tema visual de la aplicación (opcional)", example = "Rosado")
    private String tema; // OPCIONAL

    @Schema(description = "Huella digital activada o no", example = "true")
    private Boolean huellaActiva = false; // DEFAULT false
}
