package com.mentelibre.chat_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.chat_service.model.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Historial completo por usuario
    List<ChatMessage> findByUserIdOrderByTimestampAsc(Long userId);

    // Historial completo por mascota
    List<ChatMessage> findByPetIdOrderByTimestampAsc(Long petId);

    // Últimos N mensajes si los necesitas
    List<ChatMessage> findTop20ByUserIdOrderByTimestampDesc(Long userId);
}
