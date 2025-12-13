package com.mentelibre.virtualpet_service.dto;

import lombok.Data;

@Data
public class PetItemDto {
    private Long id;
    private String name;
    private String type;
    private String slot;
    private Integer price;
    private String imageUrl;
    private boolean active;
}