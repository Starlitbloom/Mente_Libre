package com.mentelibre.user_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Component
public class StorageClient {

    private final WebClient webClient;

    public StorageClient(@Value("${storage.service.url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    // =======================
    // SUBIR ARCHIVO
    public Map<String, Object> uploadFile(MultipartFile file, String category, String token) {
        try {
            return webClient.post()
                    .uri("/api/v1/storage/upload?category=" + category)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(file.getResource())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Error al subir archivo", e);
        }
    }

    // =======================
    // LISTAR MIS ARCHIVOS
    public List<Map<String, Object>> getMyFiles(String token) {
        try {
            return webClient.get()
                    .uri("/api/v1/storage/me")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener archivos", e);
        }
    }

    // =======================
    // ELIMINAR
    public boolean deleteFile(Long fileId, String token) {
        try {
            webClient.delete()
                    .uri("/api/v1/storage/" + fileId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // =======================
    // ACTUALIZAR
    public Map<String, Object> updateFile(Long fileId, MultipartFile file, String token) {
        try {
            return webClient.put()
                    .uri("/api/v1/storage/" + fileId)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(file.getResource())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar archivo", e);
        }
    }
}
