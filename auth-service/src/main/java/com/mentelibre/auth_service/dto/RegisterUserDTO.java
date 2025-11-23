package com.mentelibre.auth_service.dto;

import lombok.Data;

@Data
public class RegisterUserDTO {
    private String username;
    private String email;
    private String phone;
    private String password;
    private Long rolId;

}

