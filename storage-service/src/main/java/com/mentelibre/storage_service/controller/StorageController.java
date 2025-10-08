package com.mentelibre.storage_service.controller;

import com.mentelibre.storage_service.model.FileCategory;
import com.mentelibre.storage_service.model.FileResponse;
import com.mentelibre.storage_service.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class StorageController {

    @Autowired
    private StorageService storageService;

    // Subir archivo
    @PostMapping("/upload")
    @PreAuthorize("hasRole('STUDENT')")
    public FileResponse uploadFile(@RequestParam("file") MultipartFile file,
                                   @RequestParam("category") FileCategory category,
                                   @RequestParam("ownerId") Long ownerId) throws IOException {
        return storageService.store(file, ownerId, category);
    }

    // Listar archivos de un usuario
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public List<FileResponse> getFiles(@PathVariable Long ownerId) {
        return storageService.getFilesByOwner(ownerId);
    }

    // Eliminar archivo
    @DeleteMapping("/{fileId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public void deleteFile(@PathVariable Long fileId) throws IOException {
        storageService.deleteFile(fileId);
    }

    // Actualizar archivo
    @PutMapping("/{fileId}")
    @PreAuthorize("hasRole('STUDENT')")
    public FileResponse updateFile(@PathVariable Long fileId,
                                   @RequestParam("file") MultipartFile file) throws IOException {
        return storageService.updateFile(fileId, file);
    }
}

