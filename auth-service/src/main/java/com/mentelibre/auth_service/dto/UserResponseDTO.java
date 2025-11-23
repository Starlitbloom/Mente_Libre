package com.mentelibre.auth_service.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String  phone;
    private String rol; // nombre del rol
}
