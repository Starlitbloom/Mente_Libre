package com.mentelibre.user_service.dto;

import java.time.LocalDate;

import com.mentelibre.user_service.model.Genero;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private String username;
    private String email;
    private String phone;
    private String fotoPerfil;
    private LocalDate fechaNacimiento;
    private String direccion;
    private Boolean notificaciones;
    private Genero genero;
}
