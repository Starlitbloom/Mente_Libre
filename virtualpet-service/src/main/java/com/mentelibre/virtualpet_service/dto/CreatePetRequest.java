package com.mentelibre.virtualpet_service.dto;

import lombok.Data;

@Data
public class CreatePetRequest {
    private String name;
    private String type;
    private String avatarKey;
}