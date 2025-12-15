package com.mentelibre.emotion_service.dto;

import lombok.Data;

@Data
public class AuthValidationResponse {
    private Long userId;
    private String rol;
}