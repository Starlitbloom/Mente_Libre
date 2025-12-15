package com.mentelibre.chat_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatResponseDto {
    private Long messageId;
    private String petResponse;
    private LocalDateTime timestamp;
}
