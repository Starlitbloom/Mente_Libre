package com.mentelibre.chat_service.controller;

import com.mentelibre.chat_service.dto.ChatRequestDto;
import com.mentelibre.chat_service.dto.ChatResponseDto;
import com.mentelibre.chat_service.model.ChatMessage;
import com.mentelibre.chat_service.service.ChatService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")  // Permite que tu app Android llame al microservicio
public class ChatController {

    private final ChatService chatService;

    /** 🔹 Enviar un mensaje al chat */
    @PostMapping("/send")
    public ChatResponseDto sendMessage(@RequestBody ChatRequestDto request) {
        return chatService.processUserMessage(request);
    }

    /** 🔹 Obtener historial completo del usuario */
    @GetMapping("/history/{userId}")
    public List<ChatMessage> getHistory(@PathVariable Long userId) {
        return chatService.getChatHistory(userId);
    }
}
