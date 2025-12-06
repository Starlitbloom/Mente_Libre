package com.mentelibre.user_service.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos opcionales para actualizar un perfil de usuario")
public class UpdateUserProfileRequestDto {

    @Size(max = 255)
    @Schema(description = "URL de foto de perfil", example = "https://url.com/nuevaFoto.png")
    private String fotoPerfil;

    @Past
    @Schema(description = "Fecha de nacimiento", example = "2006-03-11")
    private LocalDate fechaNacimiento;

    @Size(max = 50)
    @Schema(description = "Dirección del usuario", example = "Nueva dirección 123")
    private String direccion;

    @Schema(description = "Notificaciones activadas o no", example = "true")
    private Boolean notificaciones;

    @Schema(description = "ID del género", example = "2")
    private Long generoId;

    @Size(max = 100)
    @Schema(description = "Objetivo de salud del usuario", example = "Dormir mejor")
    private String objetivo;

    @Size(max = 50)
    @Schema(description = "Tema visual", example = "Azul")
    private String tema;

    @Schema(description = "Huella digital activada o no", example = "false")
    private Boolean huellaActiva;
}
