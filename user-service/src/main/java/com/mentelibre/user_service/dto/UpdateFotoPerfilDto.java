package com.mentelibre.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateFotoPerfilDto {

    @NotBlank(message = "La URL de la foto no puede estar vacía")
    @Pattern(
        regexp = "^(https?)://.*$",
        message = "La URL debe comenzar con http o https"
    )
    private String fotoPerfil;
}
