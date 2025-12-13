package com.mentelibre.virtualpet_service.dto;

import lombok.Data;

@Data
public class PetDto {
    private Long id;
    private Long userId;
    private String name;
    private String type;
    private String avatarKey;

    private int points;
    private int level;
    private int experience;

    private int affinity;
    private int energy;
}