package com.mentelibre.emotion_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class StorageClient {

    private final WebClient webClient;

    public StorageClient(@Value("${storage.service.url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    // Listar archivos de un usuario
    public List<Object> getFilesByOwner(Long ownerId, String token) {
        try {
            return webClient.get()
                    .uri("/api/files/owner/{ownerId}", ownerId)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToFlux(Object.class) // usamos Object porque no traemos modelo
                    .collectList()
                    .block();
        } catch (Exception e) {
            return List.of();
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

    // Subir archivo
    public Object uploadFile(MultipartFile file, Long ownerId, String category, String token) {
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
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }

    // Actualizar archivo
    public Object updateFile(Long fileId, MultipartFile file, String token) {
        try {
            return webClient.put()
                    .uri("/api/files/{fileId}", fileId)
                    .header("Authorization", token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(file.getResource())
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }
}
