package com.mentelibre.emotion_service.service;

import com.mentelibre.emotion_service.model.Emotion;
import com.mentelibre.emotion_service.dto.EmotionSummaryDTO;
import com.mentelibre.emotion_service.repository.EmotionRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmotionService {

    private final EmotionRepository emotionRepository;

    public EmotionService(EmotionRepository emotionRepository) {
        this.emotionRepository = emotionRepository;
    }

    public Emotion crearEmotion(Emotion e) {
        e.setFechaRegistro(LocalDateTime.now());
        return emotionRepository.save(e);
    }

    public List<Emotion> obtenerEmotionsPorUsuario(Long userId) {
        return emotionRepository.findByUserId(userId);
    }

    // 🔹 Obtener emociones agrupadas por día
    public List<EmotionSummaryDTO> obtenerResumenSemanal(Long userId) {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioSemana = hoy.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime finSemana = inicioSemana.plusDays(6).with(LocalTime.MAX);

        List<Emotion> emociones = emotionRepository.findByUserIdAndFechaBetween(userId, inicioSemana, finSemana);

        return emociones.stream()
                .collect(Collectors.groupingBy(e -> e.getFechaRegistro().toLocalDate()))
                .entrySet().stream()
                .map(entry -> {
                    List<Emotion> lista = entry.getValue();
                    double promedio = lista.stream().mapToInt(Emotion::getIntensidad).average().orElse(0);
                    String tipoPredominante = lista.stream()
                            .collect(Collectors.groupingBy(Emotion::getTipo, Collectors.counting()))
                            .entrySet().stream().max(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey).orElse("Desconocido");

                    return new EmotionSummaryDTO(entry.getKey(), tipoPredominante, promedio);
                })
                .sorted(Comparator.comparing(EmotionSummaryDTO::getFecha))
                .collect(Collectors.toList());
    }
}