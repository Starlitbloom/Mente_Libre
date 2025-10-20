package com.mentelibre.storage_service.service;

import com.mentelibre.storage_service.model.FileCategory;
import com.mentelibre.storage_service.model.FileEntity;
import com.mentelibre.storage_service.model.FileResponse;
import com.mentelibre.storage_service.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StorageServiceTest {

    private FileRepository fileRepository;
    private StorageService storageService;

    @BeforeEach
    void setUp() throws IOException {
        fileRepository = mock(FileRepository.class);
        storageService = new StorageService();
        storageService = Mockito.spy(storageService);
        storageService.fileRepository = fileRepository; // inyectar repo mock
    }

    @Test
    void testStoreFile() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "foto.png", "image/png", "contenido".getBytes());
        FileEntity savedEntity = new FileEntity(1L, "foto.png", "image/png", "uploads/foto.png", 1L, FileCategory.PROFILE, null, null);

        doReturn(savedEntity).when(fileRepository).save(any(FileEntity.class));

        FileResponse response = storageService.store(multipartFile, 1L, FileCategory.PROFILE);

        assertNotNull(response);
        assertEquals("foto.png", response.getFileName());
        verify(fileRepository, times(1)).save(any(FileEntity.class));
    }

    @Test
    void testGetFilesByOwner() {
        FileEntity f1 = new FileEntity(1L, "foto1.png", "image/png", "uploads/foto1.png", 1L, FileCategory.PROFILE, null, null);
        FileEntity f2 = new FileEntity(2L, "foto2.png", "image/png", "uploads/foto2.png", 1L, FileCategory.GRATITUDE, null, null);

        when(fileRepository.findByOwnerId(1L)).thenReturn(List.of(f1, f2));

        List<FileResponse> files = storageService.getFilesByOwner(1L);

        assertEquals(2, files.size());
        assertEquals("foto1.png", files.get(0).getFileName());
        verify(fileRepository, times(1)).findByOwnerId(1L);
    }

    @Test
    void testDeleteFile() throws IOException {
        FileEntity entity = new FileEntity(1L, "foto.png", "image/png", "uploads/foto.png", 1L, FileCategory.PROFILE, null, null);

        when(fileRepository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(fileRepository).delete(entity);

        // Crear archivo temporal para eliminar
        Path temp = Path.of("uploads/foto.png");
        Files.createDirectories(temp.getParent());
        Files.write(temp, "contenido".getBytes());

        storageService.deleteFile(1L);

        verify(fileRepository, times(1)).findById(1L);
        verify(fileRepository, times(1)).delete(entity);
        assertFalse(Files.exists(temp));
    }

    @Test
    void testUpdateFile() throws IOException {
        FileEntity entity = new FileEntity(1L, "old.png", "image/png", "uploads/old.png", 1L, FileCategory.PROFILE, null, null);
        MockMultipartFile newFile = new MockMultipartFile("file", "new.png", "image/png", "contenido".getBytes());
        FileEntity savedEntity = new FileEntity(1L, "new.png", "image/png", "uploads/new.png", 1L, FileCategory.PROFILE, null, null);

        when(fileRepository.findById(1L)).thenReturn(Optional.of(entity));
        doReturn(savedEntity).when(fileRepository).save(any(FileEntity.class));

        // Crear archivo temporal viejo
        Path temp = Path.of("uploads/old.png");
        Files.createDirectories(temp.getParent());
        Files.write(temp, "contenido".getBytes());

        FileResponse response = storageService.updateFile(1L, newFile);

        assertNotNull(response);
        assertEquals("new.png", response.getFileName());
        verify(fileRepository, times(1)).findById(1L);
        verify(fileRepository, times(1)).save(entity);
        assertFalse(Files.exists(Path.of("uploads/old.png")));
    }
}
