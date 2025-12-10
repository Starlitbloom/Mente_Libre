package com.mentelibre.user_service.config; // o donde corresponda

public class AuthValidationResponse {

    private Long userId;
    private String rol;

    public Long getUserId() {
        return userId;
    }

    public String getRol() {
        return rol;
    }
}
