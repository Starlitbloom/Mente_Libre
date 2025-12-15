package com.mentelibre.chat_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mentelibre.chat_service.dto.ChatRequestDto;
import com.mentelibre.chat_service.dto.ChatResponseDto;
import com.mentelibre.chat_service.model.ChatMessage;
import com.mentelibre.chat_service.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatRepo;

    public ChatResponseDto processUserMessage(ChatRequestDto request) {

        Long userId = request.getUserId();
        Long petId = request.getPetId();
        String userMessage = request.getUserMessage();

        // ← Aquí luego integrarás OpenAI / Gemini
        String iaResponse = generateAIResponse(userMessage);

        // Guardar mensaje en BD
        ChatMessage msg = new ChatMessage();
        msg.setUserId(userId);
        msg.setPetId(petId);
        msg.setUserMessage(userMessage);
        msg.setPetResponse(iaResponse);

        chatRepo.save(msg);

        // Respuesta al cliente
        ChatResponseDto response = new ChatResponseDto();
        response.setMessageId(msg.getId());
        response.setPetResponse(iaResponse);
        response.setTimestamp(msg.getTimestamp());

        return response;
    }

    // Respuesta temporal mientras no haya IA real
    private String generateAIResponse(String userMessage) {
        return "Tu compañera te dice: " + userMessage;
    }

    public List<ChatMessage> getChatHistory(Long userId) {
        return chatRepo.findByUserIdOrderByTimestampAsc(userId);
    }
}
