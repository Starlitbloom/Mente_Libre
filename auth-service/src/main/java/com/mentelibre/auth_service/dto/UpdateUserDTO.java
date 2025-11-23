package com.mentelibre.auth_service.dto;

import lombok.Data;

@Data
public class UpdateUserDTO {
    private String username;
    private String email;
    private String phone;
    private Long rolId;  
}

