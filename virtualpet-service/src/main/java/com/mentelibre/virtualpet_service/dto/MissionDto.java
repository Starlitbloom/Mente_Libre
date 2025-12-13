package com.mentelibre.virtualpet_service.dto;

import lombok.Data;

@Data
public class MissionDto {
    private Long id;
    private String title;
    private String description;
    private String type;
    private int pointsReward;
}