package com.mentelibre.storage_service.service;

import com.mentelibre.storage_service.model.FileCategory;
import com.mentelibre.storage_service.model.FileEntity;
import com.mentelibre.storage_service.model.FileResponse;
import com.mentelibre.storage_service.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StorageServiceTest {

    private FileRepository fileRepository;
    private StorageService storageService;

    @TempDir
    Path tempDir; // carpeta temporal para los tests de archivos

    @Test
    void testStoreFile() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "foto.png", "image/png", "contenido".getBytes());

        FileEntity savedEntity = new FileEntity(1L, "foto.png", "image/png",
                tempDir.resolve("foto.png").toString(), 1L, FileCategory.PROFILE, null, null);

        when(fileRepository.save(any(FileEntity.class))).thenReturn(savedEntity);

        FileResponse response = storageService.store(multipartFile, 1L, FileCategory.PROFILE);

        assertNotNull(response);
        assertEquals("foto.png", response.getFileName());
        verify(fileRepository, times(1)).save(any(FileEntity.class));
    }

    @Test
    void testGetFilesByOwner() {
        FileEntity f1 = new FileEntity(1L, "foto1.png", "image/png",
                tempDir.resolve("foto1.png").toString(), 1L, FileCategory.PROFILE, null, null);
        FileEntity f2 = new FileEntity(2L, "foto2.png", "image/png",
                tempDir.resolve("foto2.png").toString(), 1L, FileCategory.GRATITUDE, null, null);

        when(fileRepository.findByOwnerId(1L)).thenReturn(List.of(f1, f2));

        List<FileResponse> files = storageService.getFilesByOwner(1L);

        assertEquals(2, files.size());
        assertEquals("foto1.png", files.get(0).getFileName());
        verify(fileRepository, times(1)).findByOwnerId(1L);
    }

    @Test
    void testDeleteFile() throws IOException {
        FileEntity entity = new FileEntity(1L, "foto.png", "image/png",
                tempDir.resolve("foto.png").toString(), 1L, FileCategory.PROFILE, null, null);

        when(fileRepository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(fileRepository).delete(entity);

        // crear archivo temporal
        Path tempFile = tempDir.resolve("foto.png");
        java.nio.file.Files.write(tempFile, "contenido".getBytes());

        storageService.deleteFile(1L);

        verify(fileRepository, times(1)).findById(1L);
        verify(fileRepository, times(1)).delete(entity);
        assertFalse(java.nio.file.Files.exists(tempFile));
    }

    @Test
    void testUpdateFile() throws IOException {
        FileEntity entity = new FileEntity(1L, "old.png", "image/png",
                tempDir.resolve("old.png").toString(), 1L, FileCategory.PROFILE, null, null);

        MockMultipartFile newFile = new MockMultipartFile("file", "new.png", "image/png", "contenido".getBytes());

        FileEntity savedEntity = new FileEntity(1L, "new.png", "image/png",
                tempDir.resolve("new.png").toString(), 1L, FileCategory.PROFILE, null, null);

        when(fileRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(fileRepository.save(any(FileEntity.class))).thenReturn(savedEntity);

        // crear archivo viejo
        java.nio.file.Files.write(tempDir.resolve("old.png"), "contenido".getBytes());

        FileResponse response = storageService.updateFile(1L, newFile);

        assertNotNull(response);
        assertEquals("new.png", response.getFileName());
        verify(fileRepository, times(1)).findById(1L);
        verify(fileRepository, times(1)).save(entity);
        assertFalse(java.nio.file.Files.exists(tempDir.resolve("old.png")));
    }
}
