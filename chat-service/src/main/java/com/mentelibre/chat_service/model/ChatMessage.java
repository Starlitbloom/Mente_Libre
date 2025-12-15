package com.mentelibre.chat_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;     // ID del usuario que escribe
    private Long petId;      // ID de la mascota del usuario

    @Column(columnDefinition = "TEXT")
    private String userMessage;

    @Column(columnDefinition = "TEXT")
    private String petResponse;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}
