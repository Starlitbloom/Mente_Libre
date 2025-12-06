package com.mentelibre.storage_service.controller;

import com.mentelibre.storage_service.dto.FileResponse;
import com.mentelibre.storage_service.model.FileCategory;
import com.mentelibre.storage_service.service.StorageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import org.springframework.mock.web.MockMultipartFile;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(
        controllers = StorageController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                com.mentelibre.storage_service.config.SecurityConfig.class,
                                com.mentelibre.storage_service.config.JwtRequestFilter.class
                        }
                )
        }
)
@AutoConfigureMockMvc(addFilters = false) // DESACTIVA SEGURIDAD para los tests
class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StorageService storageService;

    // ---------- MOCK DE AUTH ----------
    private void mockAuth(Long userId) {
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userId);

        SecurityContext ctx = Mockito.mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(ctx);
    }

    @BeforeEach
    void setup() {
        mockAuth(10L); // Usuario logueado con ID = 10
    }

    // ============================================================
    // SUBIR ARCHIVO
    @Test
    void uploadFile_returnCreated() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file", "foto.png", "image/png", "contenido".getBytes()
        );

        FileResponse response =
                new FileResponse(1L, "foto.png", "/uploads/foto.png", FileCategory.PROFILE);

        when(storageService.store(any(), anyLong(), any(FileCategory.class)))
                .thenReturn(response);

        mockMvc.perform(
                multipart("/api/v1/storage/upload")
                        .file(file)
                        .param("category", "PROFILE")
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("foto.png"));
    }

    // ============================================================
    // LISTAR MIS ARCHIVOS
    @Test
    void listMyFiles_returnOK() throws Exception {

        FileResponse f1 = new FileResponse(1L, "a.png", "/uploads/a.png", FileCategory.PROFILE);
        FileResponse f2 = new FileResponse(2L, "b.png", "/uploads/b.png", FileCategory.GRATITUDE);

        when(storageService.getFilesByOwner(10L))
                .thenReturn(List.of(f1, f2));

        mockMvc.perform(get("/api/v1/storage/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    // ============================================================
    // LISTAR MIS ARCHIVOS - NO CONTENT
    @Test
    void listMyFiles_noContent() throws Exception {

        when(storageService.getFilesByOwner(10L))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/storage/me"))
                .andExpect(status().isNoContent());
    }

    // ============================================================
    // ACTUALIZAR ARCHIVO
    @Test
    void updateFile_returnOK() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file", "new.png", "image/png", "abc".getBytes()
        );

        FileResponse updated =
                new FileResponse(1L, "new.png", "/uploads/new.png", FileCategory.PROFILE);

        when(storageService.updateFile(eq(1L), any(), eq(10L)))
                .thenReturn(updated);

        mockMvc.perform(
                multipart("/api/v1/storage/1")
                        .file(file)
                        .with(req -> { req.setMethod("PUT"); return req; })
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("new.png"));
    }

    // ============================================================
    // ELIMINAR ARCHIVO
    @Test
    void deleteFile_returnOK() throws Exception {

        mockMvc.perform(delete("/api/v1/storage/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Archivo eliminado correctamente"));

        verify(storageService).deleteFile(1L, 10L);
    }

    // ============================================================
    // ERRORES
    @Test
    void uploadFile_invalidType_returnBadRequest() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file", "virus.exe", "application/octet-stream", "abc".getBytes()
        );

        when(storageService.store(any(), anyLong(), any(FileCategory.class)))
                .thenThrow(new RuntimeException("Solo se permiten archivos de imagen"));

        mockMvc.perform(
                multipart("/api/v1/storage/upload")
                        .file(file)
                        .param("category", "PROFILE")
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Solo se permiten archivos de imagen"));
    }

    @Test
    void updateFile_notOwner_returnBadRequest() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file", "new.png", "image/png", "abc".getBytes()
        );

        when(storageService.updateFile(eq(1L), any(), eq(10L)))
                .thenThrow(new RuntimeException("No tienes permiso para modificar este archivo"));

        mockMvc.perform(
                multipart("/api/v1/storage/1")
                        .file(file)
                        .with(req -> { req.setMethod("PUT"); return req; })
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No tienes permiso para modificar este archivo"));
    }

    @Test
    void deleteFile_notFound_returnBadRequest() throws Exception {

        doThrow(new RuntimeException("Archivo no encontrado"))
                .when(storageService)
                .deleteFile(1L, 10L);

        mockMvc.perform(delete("/api/v1/storage/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Archivo no encontrado"));
    }

    @Test
    void uploadFile_tooLarge_returnBadRequest() throws Exception {

        byte[] bigFile = new byte[6 * 1024 * 1024]; // 6 MB

        MockMultipartFile file = new MockMultipartFile(
                "file", "giant.png", "image/png", bigFile
        );

        when(storageService.store(any(), anyLong(), any(FileCategory.class)))
                .thenThrow(new RuntimeException("El archivo supera el máximo permitido (5MB)"));

        mockMvc.perform(
                multipart("/api/v1/storage/upload")
                        .file(file)
                        .param("category", "PROFILE")
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El archivo supera el máximo permitido (5MB)"));
    }

}
