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

    // Subir archivo
    public Map<String, Object> uploadFile(MultipartFile file, Long ownerId, String category, String token) {
        try {
            return webClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/api/files/upload")
                            .queryParam("ownerId", ownerId)
                            .queryParam("category", category)
                            .build())
                    .header("Authorization", token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(file.getResource())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Error al subir archivo", e);
        }
    }

    // Listar archivos de un usuario
    public List<Map<String, Object>> getFilesByOwner(Long ownerId, String token) {
        try {
            return webClient.get()
                    .uri("/api/files/owner/{ownerId}", ownerId)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener archivos", e);
        }
    }

    // Eliminar archivo
    public boolean deleteFile(Long fileId, String token) {
        try {
            webClient.delete()
                    .uri("/api/files/{fileId}", fileId)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Actualizar archivo
    public Map<String, Object> updateFile(Long fileId, MultipartFile file, String token) {
        try {
            return webClient.put()
                    .uri("/api/files/{fileId}", fileId)
                    .header("Authorization", token)
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
