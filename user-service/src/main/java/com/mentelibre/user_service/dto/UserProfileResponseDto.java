package com.mentelibre.user_service.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Respuesta completa del perfil del usuario")
public class UserProfileResponseDto {

    @Schema(description = "ID del perfil", example = "4")
    private Long id;

    @Schema(description = "ID del usuario", example = "12")
    private Long userId;

    @Schema(description = "URL foto de perfil", example = "https://url.com/foto.png")
    private String fotoPerfil;

    private LocalDate fechaNacimiento;

    private String direccion;

    private Boolean notificaciones;

    private GeneroDTO genero;

    private String objetivo;

    private String tema;

    private Boolean huellaActiva;
}
