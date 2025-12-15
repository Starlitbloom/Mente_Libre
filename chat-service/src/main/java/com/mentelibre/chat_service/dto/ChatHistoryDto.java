package com.mentelibre.chat_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatHistoryDto {
    private Long id;
    private String userMessage;
    private String petResponse;
    private LocalDateTime timestamp;
}
