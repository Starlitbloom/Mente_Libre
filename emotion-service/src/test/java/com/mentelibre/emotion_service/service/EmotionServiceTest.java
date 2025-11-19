package com.mentelibre.emotion_service.service;

import com.mentelibre.emotion_service.model.Emotion;
import com.mentelibre.emotion_service.dto.EmotionSummaryDTO;
import com.mentelibre.emotion_service.repository.EmotionRepository;
import com.mentelibre.emotion_service.webclient.AuthClient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmotionServiceTest {

    @Mock
    private EmotionRepository emotionRepository;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private EmotionService emotionService;


    // ==========================================================
    // 1) Crear emoción - usuario válido
    // ==========================================================
    @Test
    void crearEmotion_usuarioValido_guardaEmotion() {

        Emotion e = new Emotion();
        e.setUserId(1L);
        e.setTipo("Feliz");
        e.setIntensidad(8);

        when(authClient.existeUsuario(1L)).thenReturn(true);
        when(emotionRepository.save(e)).thenReturn(e);

        Emotion resultado = emotionService.crearEmotion(e);

        assertNotNull(resultado);
        verify(authClient, times(1)).existeUsuario(1L);
        verify(emotionRepository, times(1)).save(e);
    }


    // ==========================================================
    // 2) Crear emoción - usuario NO existe
    // ==========================================================
    @Test
    void crearEmotion_usuarioInvalido_lanzaExcepcion() {

        Emotion e = new Emotion();
        e.setUserId(99L);

        when(authClient.existeUsuario(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            emotionService.crearEmotion(e);
        });

        assertEquals("El usuario no existe en Auth Service.", ex.getMessage());
        verify(emotionRepository, never()).save(any());
    }


    // ==========================================================
    // 3) Obtener emociones por usuario
    // ==========================================================
    @Test
    void obtenerEmotionsPorUsuario_devuelveLista() {

        Emotion e1 = new Emotion();
        e1.setId(1L);
        e1.setUserId(1L);
        e1.setTipo("Triste");
        e1.setIntensidad(3);
        e1.setFechaRegistro(LocalDateTime.now());

        Emotion e2 = new Emotion();
        e2.setId(2L);
        e2.setUserId(1L);
        e2.setTipo("Feliz");
        e2.setIntensidad(8);
        e2.setFechaRegistro(LocalDateTime.now());

        List<Emotion> lista = Arrays.asList(e1, e2);

        when(emotionRepository.findByUserId(1L)).thenReturn(lista);

        List<Emotion> resultado = emotionService.obtenerEmotionsPorUsuario(1L);

        assertEquals(2, resultado.size());
        verify(emotionRepository, times(1)).findByUserId(1L);
    }

    // ==========================================================
    // 4) Resumen semanal - agrupación correcta
    // ==========================================================
    @Test
    void obtenerResumenSemanal_devuelveResumenCorrecto() {

        Long userId = 1L;
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);

        Emotion e1 = new Emotion();
        e1.setId(1L);
        e1.setUserId(userId);
        e1.setTipo("Feliz");
        e1.setIntensidad(8);
        e1.setFechaRegistro(monday.atTime(10, 0));

        Emotion e2 = new Emotion();
        e2.setId(2L);
        e2.setUserId(userId);
        e2.setTipo("Feliz");
        e2.setIntensidad(6);
        e2.setFechaRegistro(monday.atTime(12, 0));

        Emotion e3 = new Emotion();
        e3.setId(3L);
        e3.setUserId(userId);
        e3.setTipo("Triste");
        e3.setIntensidad(2);
        e3.setFechaRegistro(monday.plusDays(1).atTime(15, 0));

        List<Emotion> mockEmotions = Arrays.asList(e1, e2, e3);

        when(emotionRepository.findByUserIdAndFechaBetween(
                eq(userId),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(mockEmotions);

        List<EmotionSummaryDTO> resumen = emotionService.obtenerResumenSemanal(userId);

        assertEquals(2, resumen.size());

        EmotionSummaryDTO dia1 = resumen.get(0);
        assertEquals("Feliz", dia1.getTipoPredominante());
        assertEquals(7.0, dia1.getPromedioIntensidad());

        EmotionSummaryDTO dia2 = resumen.get(1);
        assertEquals("Triste", dia2.getTipoPredominante());
        assertEquals(2.0, dia2.getPromedioIntensidad());
    }

}
