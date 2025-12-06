package com.mentelibre.storage_service.service;

import com.mentelibre.storage_service.dto.FileResponse;
import com.mentelibre.storage_service.model.FileCategory;
import com.mentelibre.storage_service.model.FileEntity;
import com.mentelibre.storage_service.repository.FileRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private FileRepository fileRepository;

    private StorageService storageService;
    private Path tempDir;

    @BeforeEach
    void setup() throws IOException {
        tempDir = Files.createTempDirectory("uploads-test");
        storageService = new StorageService(tempDir, fileRepository);
    }

    @Test
    void storeFile_success() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "foto.jpg", "image/jpeg", "abc".getBytes()
        );

        FileEntity saved = new FileEntity(
                1L, "foto.jpg", "image/jpeg", "/uploads/foto.jpg",
                5L, FileCategory.PROFILE, null, null);

        when(fileRepository.save(any(FileEntity.class))).thenReturn(saved);

        FileResponse response = storageService.store(file, 5L, FileCategory.PROFILE);

        assertNotNull(response);
        assertEquals("foto.jpg", response.getFileName());
        verify(fileRepository).save(any(FileEntity.class));
    }

    @Test
    void storeFile_invalidType_throwsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "malware.exe", "application/octet-stream", "abc".getBytes()
        );

        Exception ex = assertThrows(RuntimeException.class, () ->
                storageService.store(file, 5L, FileCategory.PROFILE)
        );

        assertEquals("Solo se permiten archivos de imagen", ex.getMessage());
    }

    @Test
    void getFilesByOwner_success() {
        FileEntity f1 = new FileEntity(1L, "a.png", "image/png", "/uploads/a.png", 5L, FileCategory.PROFILE, null, null);
        FileEntity f2 = new FileEntity(2L, "b.png", "image/png", "/uploads/b.png", 5L, FileCategory.GRATITUDE, null, null);

        when(fileRepository.findByOwnerId(5L)).thenReturn(List.of(f1, f2));

        List<FileResponse> res = storageService.getFilesByOwner(5L);

        assertEquals(2, res.size());
        verify(fileRepository).findByOwnerId(5L);
    }

    @Test
    void deleteFile_success() throws IOException {
        FileEntity entity = new FileEntity(
                1L, "a.png", "image/png",
                "/uploads/a.png", 5L, FileCategory.PROFILE, null, null
        );

        // archivo real en temp
        Path file = tempDir.resolve("a.png");
        Files.write(file, "abc".getBytes());

        when(fileRepository.findById(1L)).thenReturn(Optional.of(entity));

        storageService.deleteFile(1L, 5L);

        assertFalse(Files.exists(file));
        verify(fileRepository).delete(entity);
    }

    @Test
    void updateFile_wrongOwner_throwsException() {
        FileEntity entity = new FileEntity(
                1L, "old.png", "image/png",
                "/uploads/old.png", 99L, FileCategory.PROFILE, null, null
        );

        MockMultipartFile newFile = new MockMultipartFile(
                "file", "new.png", "image/png", "abc".getBytes()
        );

        when(fileRepository.findById(1L)).thenReturn(Optional.of(entity));

        Exception ex = assertThrows(RuntimeException.class, () ->
                storageService.updateFile(1L, newFile, 5L)
        );

        assertEquals("No tienes permiso para modificar este archivo", ex.getMessage());
    }
}
