package com.mentelibre.virtualpet_service.dto;

import lombok.Data;

@Data
public class UpdatePetRequest {
    private String name;
    private String avatarKey;
}