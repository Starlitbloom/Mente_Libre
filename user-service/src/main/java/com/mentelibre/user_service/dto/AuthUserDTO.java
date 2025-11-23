package com.mentelibre.user_service.dto;

import lombok.Data;

@Data
public class AuthUserDTO {
    private String username;
    private String email;
    private String phone;
}
