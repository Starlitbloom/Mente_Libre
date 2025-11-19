package com.mentelibre.storage_service.service;

import com.mentelibre.storage_service.model.FileCategory;
import com.mentelibre.storage_service.model.FileEntity;
import com.mentelibre.storage_service.model.FileResponse;
import com.mentelibre.storage_service.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageService {

    private final Path root = Paths.get("uploads");

    @Autowired
    private FileRepository fileRepository;

    public StorageService() throws IOException {
        Files.createDirectories(root); // crea la carpeta uploads si no existe
    }

    // Subir archivo
    public FileResponse store(MultipartFile file, Long ownerId, FileCategory category) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path targetLocation = this.root.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        FileEntity entity = new FileEntity();
        entity.setFileName(file.getOriginalFilename());
        entity.setFileType(file.getContentType());
        entity.setUrl(targetLocation.toString());
        entity.setOwnerId(ownerId);
        entity.setCategory(category);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        FileEntity saved = fileRepository.save(entity);
        return toResponse(saved);
    }

    // Listar archivos de un usuario
    public List<FileResponse> getFilesByOwner(Long ownerId) {
        List<FileEntity> files = fileRepository.findByOwnerId(ownerId);
        return files.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Eliminar archivo
    public void deleteFile(Long fileId) throws IOException {
        FileEntity entity = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));
        Files.deleteIfExists(Paths.get(entity.getUrl()));
        fileRepository.delete(entity);
    }

    // Actualizar archivo (reemplaza el archivo existente)
    public FileResponse updateFile(Long fileId, MultipartFile file) throws IOException {
        FileEntity entity = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

        // eliminar archivo antiguo
        Files.deleteIfExists(Paths.get(entity.getUrl()));

        // guardar nuevo archivo
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path targetLocation = this.root.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        entity.setFileName(file.getOriginalFilename());
        entity.setFileType(file.getContentType());
        entity.setUrl(targetLocation.toString());
        entity.setUpdatedAt(LocalDateTime.now());

        FileEntity saved = fileRepository.save(entity);
        return toResponse(saved);
    }
    

    // Convertir FileEntity a FileResponse
    private FileResponse toResponse(FileEntity file) {
        return new FileResponse(file.getId(), file.getFileName(), file.getUrl(), file.getCategory());
    }
}
