package com.mentelibre.virtualpet_service.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserProfileResponseDto {
    private Long id;
    private Long userId;
    private String fotoPerfil;
    private LocalDate fechaNacimiento;
    private String direccion;
    private Boolean notificaciones;
    private GeneroDto genero;
    private String objetivo;
    private String tema;
    private Boolean huellaActiva;
}