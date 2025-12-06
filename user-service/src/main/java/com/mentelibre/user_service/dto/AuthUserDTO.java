package com.mentelibre.user_service.dto;

import lombok.Data;

@Data
public class AuthUserDTO {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String rol; // IMPORTANTE viene desde Auth-Service
}
