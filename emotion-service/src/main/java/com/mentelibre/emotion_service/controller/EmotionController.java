package com.mentelibre.emotion_service.controller;

import com.mentelibre.emotion_service.model.Emotion;
import com.mentelibre.emotion_service.dto.EmotionSummaryDTO;
import com.mentelibre.emotion_service.service.EmotionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/emotions")
public class EmotionController {

    private final EmotionService emotionService;

    public EmotionController(EmotionService emotionService) {
        this.emotionService = emotionService;
    }

    @PostMapping
    @Operation(summary = "Registrar una nueva emoción del usuario")
    public ResponseEntity<Emotion> crearEmotion(@RequestBody Emotion emotion) {
        Emotion nueva = emotionService.crearEmotion(emotion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener todas las emociones de un usuario")
    public ResponseEntity<List<Emotion>> listarEmotionsPorUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(emotionService.obtenerEmotionsPorUsuario(userId));
    }

    @GetMapping("/summary/week/{userId}")
    @Operation(summary = "Obtener resumen de emociones de la semana actual")
    public ResponseEntity<List<EmotionSummaryDTO>> resumenSemanal(@PathVariable Long userId) {
        return ResponseEntity.ok(emotionService.obtenerResumenSemanal(userId));
    }
}
