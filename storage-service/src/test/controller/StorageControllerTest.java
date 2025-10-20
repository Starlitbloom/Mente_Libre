package com.mentelibre.storage_service.controller;

import com.mentelibre.storage_service.model.FileCategory;
import com.mentelibre.storage_service.model.FileResponse;
import com.mentelibre.storage_service.service.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StorageController.class)
class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StorageService storageService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "foto.png", "image/png", "contenido".getBytes());
        FileResponse response = new FileResponse(1L, "foto.png", "/uploads/foto.png", FileCategory.PROFILE);

        when(storageService.store(any(), anyLong(), any(FileCategory.class))).thenReturn(response);

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .param("ownerId", "1")
                        .param("category", "PROFILE")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("foto.png"));
    }

    @Test
    void testGetFilesByOwner() throws Exception {
        FileResponse f1 = new FileResponse(1L, "foto1.png", "/uploads/foto1.png", FileCategory.PROFILE);
        FileResponse f2 = new FileResponse(2L, "foto2.png", "/uploads/foto2.png", FileCategory.GRATITUDE);

        when(storageService.getFilesByOwner(1L)).thenReturn(List.of(f1, f2));

        mockMvc.perform(get("/api/files/owner/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void testDeleteFile() throws Exception {
        mockMvc.perform(delete("/api/files/1"))
                .andExpect(status().isOk());
        Mockito.verify(storageService).deleteFile(1L);
    }

    @Test
    void testUpdateFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "new.png", "image/png", "contenido".getBytes());
        FileResponse response = new FileResponse(1L, "new.png", "/uploads/new.png", FileCategory.PROFILE);

        when(storageService.updateFile(anyLong(), any())).thenReturn(response);

        mockMvc.perform(multipart("/api/files/1").file(file).with(request -> {
                    request.setMethod("PUT"); // multipart default es POST, cambiamos a PUT
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("new.png"));
    }
}
