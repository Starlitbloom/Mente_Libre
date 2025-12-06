package com.mentelibre.storage_service.service;

import com.mentelibre.storage_service.dto.FileResponse;
import com.mentelibre.storage_service.model.FileCategory;
import com.mentelibre.storage_service.model.FileEntity;
import com.mentelibre.storage_service.repository.FileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StorageService {

    private Path root = Paths.get("uploads"); // 🔥 YA NO ES FINAL

    @Autowired
    private FileRepository fileRepository;

    // Constructor normal (PRODUCCIÓN)
    public StorageService() throws IOException {
        Files.createDirectories(root);
    }

    // Constructor especial para TESTS
    public StorageService(Path testRoot, FileRepository repo) throws IOException {
        this.root = testRoot;
        this.fileRepository = repo;
        Files.createDirectories(testRoot);
    }

    // ========================= VALIDACIONES =========================
    private void validateFile(MultipartFile file) {
        if (file.isEmpty())
            throw new RuntimeException("El archivo no puede estar vacío");

        if (file.getSize() > 5 * 1024 * 1024)
            throw new RuntimeException("El archivo supera el máximo permitido (5MB)");

        String type = file.getContentType();
        if (type == null || !type.startsWith("image/"))
            throw new RuntimeException("Solo se permiten archivos de imagen");

        List<String> allowed = List.of("image/png", "image/jpeg", "image/jpg", "image/webp");
        if (!allowed.contains(type))
            throw new RuntimeException("Tipo de imagen no permitido (usa PNG, JPG o WEBP)");
    }

    // ========================= SUBIR ARCHIVO =========================
    public FileResponse store(MultipartFile file, Long ownerId, FileCategory category) throws IOException {

        validateFile(file);

        String safeName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replace(" ", "_");
        Path target = root.resolve(safeName);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        FileEntity entity = new FileEntity();
        entity.setFileName(safeName);
        entity.setFileType(file.getContentType());
        entity.setUrl("/uploads/" + safeName);
        entity.setOwnerId(ownerId);
        entity.setCategory(category);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return toResponse(fileRepository.save(entity));
    }

    // ========================= LISTAR ARCHIVOS =========================
    public List<FileResponse> getFilesByOwner(Long ownerId) {
        return fileRepository.findByOwnerId(ownerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ========================= ACTUALIZAR ARCHIVO =========================
    public FileResponse updateFile(Long fileId, MultipartFile newFile, Long ownerId) throws IOException {

        validateFile(newFile);

        FileEntity entity = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

        if (!entity.getOwnerId().equals(ownerId))
            throw new RuntimeException("No tienes permiso para modificar este archivo");

        // eliminar archivo anterior
        Files.deleteIfExists(root.resolve(entity.getFileName()));

        String safeName = System.currentTimeMillis() + "_" + newFile.getOriginalFilename().replace(" ", "_");
        Path target = root.resolve(safeName);

        Files.copy(newFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        entity.setFileName(safeName);
        entity.setFileType(newFile.getContentType());
        entity.setUrl("/uploads/" + safeName);
        entity.setUpdatedAt(LocalDateTime.now());

        return toResponse(fileRepository.save(entity));
    }

    // ========================= ELIMINAR ARCHIVO =========================
    public void deleteFile(Long fileId, Long ownerId) throws IOException {
        FileEntity entity = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

        if (!entity.getOwnerId().equals(ownerId))
            throw new RuntimeException("No puedes eliminar archivos de otro usuario");

        Files.deleteIfExists(root.resolve(entity.getFileName()));

        fileRepository.delete(entity);
    }

    // ========================= DTO =========================
    private FileResponse toResponse(FileEntity file) {
        return new FileResponse(
                file.getId(),
                file.getFileName(),
                file.getUrl(),
                file.getCategory()
        );
    }
}
