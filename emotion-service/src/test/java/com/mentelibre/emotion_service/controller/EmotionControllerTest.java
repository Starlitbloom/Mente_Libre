package com.mentelibre.emotion_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentelibre.emotion_service.model.Emotion;
import com.mentelibre.emotion_service.service.EmotionService;
import com.mentelibre.emotion_service.dto.EmotionSummaryDTO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmotionController.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableMethodSecurity 
public class EmotionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmotionService emotionService;

    @Autowired
    private ObjectMapper objectMapper;

    // -----------------------------------------------------------
    // 2. Obtener emociones por usuario
    // -----------------------------------------------------------
    @Test
    @WithMockUser(username = "10", roles = {"CLIENTE"})   // ⬅️ USER 10 autenticado
    void testObtenerEmocionesPorUsuario() throws Exception {

        Emotion e1 = new Emotion(1L, 10L, "Feliz", 7, LocalDateTime.now(), "Trabajo");
        Emotion e2 = new Emotion(2L, 10L, "Triste", 3, LocalDateTime.now(), "Estudio");

        List<Emotion> emociones = Arrays.asList(e1, e2);

        when(emotionService.obtenerEmotionsPorUsuario(10L)).thenReturn(emociones);

        mockMvc.perform(get("/api/v1/emotions/user/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("Feliz"))
                .andExpect(jsonPath("$[0].intensidad").value(7))
                .andExpect(jsonPath("$[1].tipo").value("Triste"));
    }

    // -----------------------------------------------------------
    // 3. Resumen semanal
    // -----------------------------------------------------------
    @Test
    @WithMockUser(username = "1", roles = {"CLIENTE"})   // ⬅️ Authentication necesaria
    void testObtenerResumenSemanal() throws Exception {

        EmotionSummaryDTO resumen = new EmotionSummaryDTO(
                LocalDate.now(),
                "Feliz",
                7.5
        );

        when(emotionService.obtenerResumenSemanal(1L))
                .thenReturn(Arrays.asList(resumen));

        mockMvc.perform(get("/api/v1/emotions/summary/week/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoPredominante").value("Feliz"))
                .andExpect(jsonPath("$[0].promedioIntensidad").value(7.5));
    }
}
