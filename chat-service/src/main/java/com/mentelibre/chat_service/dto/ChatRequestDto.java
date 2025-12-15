package com.mentelibre.chat_service.dto;

import lombok.Data;

@Data
public class ChatRequestDto {
    private Long userId;
    private Long petId;
    private String userMessage;
}
