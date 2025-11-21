package com.mentelibre.emotion_service.service;

import com.mentelibre.emotion_service.model.Emotion;
import com.mentelibre.emotion_service.dto.EmotionSummaryDTO;
import com.mentelibre.emotion_service.repository.EmotionRepository;
import com.mentelibre.emotion_service.webclient.AuthClient;
import com.mentelibre.emotion_service.webclient.NotificationClient;
import com.mentelibre.emotion_service.webclient.StorageClient;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmotionService {

    private final EmotionRepository emotionRepository;
    private final AuthClient authClient;
    private final NotificationClient notificationClient;
    private final StorageClient storageClient; // 🔹 Agregado

    public EmotionService(EmotionRepository emotionRepository, 
                          AuthClient authClient,
                          NotificationClient notificationClient,
                          StorageClient storageClient) {
        this.emotionRepository = emotionRepository;
        this.authClient = authClient;
        this.notificationClient = notificationClient;
        this.storageClient = storageClient; // 🔹 Inyectado
    }

    // ---------------- Emotions ----------------

    public Emotion crearEmotion(Emotion e) {
        if (!authClient.existeUsuario(e.getUserId())) {
            throw new RuntimeException("El usuario no existe en Auth Service.");
        }
        e.setFechaRegistro(LocalDateTime.now());
        return emotionRepository.save(e);
    }

    public List<Emotion> obtenerEmotionsPorUsuario(Long userId) {
        return emotionRepository.findByUserId(userId);
    }

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

    public List<Emotion> obtenerTodas() {
        return emotionRepository.findAll();
    }

    // ---------------- NotificationClient ----------------

    public List<Object> listarReglasNotificacion(String token) {
        return notificationClient.getAllRules(token);
    }

    public Object obtenerReglaNotificacion(Long id, String token) {
        return notificationClient.getRuleById(id, token);
    }

    public Object crearReglaNotificacion(Object regla, String token) {
        return notificationClient.createRule(regla, token);
    }

    public Object actualizarReglaNotificacion(Long id, Object regla, String token) {
        return notificationClient.updateRule(id, regla, token);
    }

    public Object activarDesactivarRegla(Long id, boolean active, String token) {
        return notificationClient.setRuleActiveStatus(id, active, token);
    }

    public boolean eliminarRegla(Long id, String token) {
        return notificationClient.deleteRule(id, token);
    }

    // ---------------- StorageClient ----------------

    public List<Object> listarArchivos(Long ownerId, String token) {
        return storageClient.getFilesByOwner(ownerId, token);
    }

    public Object subirArchivo(MultipartFile file, Long ownerId, String category, String token) {
        return storageClient.uploadFile(file, ownerId, category, token);
    }

    public Object actualizarArchivo(Long fileId, MultipartFile file, String token) {
        return storageClient.updateFile(fileId, file, token);
    }

    public boolean eliminarArchivo(Long fileId, String token) {
        return storageClient.deleteFile(fileId, token);
    }
}
