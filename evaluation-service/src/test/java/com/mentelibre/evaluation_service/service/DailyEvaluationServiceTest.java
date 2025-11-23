package com.mentelibre.evaluation_service.service;

import com.mentelibre.evaluation_service.dto.DailyEvaluationRequest;
import com.mentelibre.evaluation_service.dto.DailyEvaluationResponse;
import com.mentelibre.evaluation_service.model.DailyEvaluation;
import com.mentelibre.evaluation_service.model.MoodLabel;
import com.mentelibre.evaluation_service.repository.DailyEvaluationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyEvaluationServiceTest {

    @Mock
    private DailyEvaluationRepository dailyEvaluationRepository;

    @InjectMocks
    private DailyEvaluationService dailyEvaluationService;

    @Test
    void upsertDailyEvaluation_creaNuevaCuandoNoExiste() {
        // given
        String userId = "user-123";
        LocalDate date = LocalDate.of(2025, 11, 23);

        DailyEvaluationRequest request = new DailyEvaluationRequest();
        request.setDate(date);
        request.setMoodLabel("FELIZ");
        request.setGlobalScore(80);
        request.setReflection("Me sentí bastante bien hoy.");

        when(dailyEvaluationRepository.findByUserIdAndDate(userId, date))
                .thenReturn(Optional.empty());

        ArgumentCaptor<DailyEvaluation> captor = ArgumentCaptor.forClass(DailyEvaluation.class);

        DailyEvaluation savedEntity = DailyEvaluation.builder()
                .id(1L)
                .userId(userId)
                .date(date)
                .mainMood(MoodLabel.FELIZ)
                .globalScore(80)
                .reflection("Me sentí bastante bien hoy.")
                .build();

        when(dailyEvaluationRepository.save(any(DailyEvaluation.class)))
                .thenReturn(savedEntity);

        // when
        DailyEvaluationResponse response =
                dailyEvaluationService.upsertDailyEvaluation(userId, request);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(userId, response.getUserId());
        assertEquals(date, response.getDate());
        assertEquals("FELIZ", response.getMoodLabel());
        assertEquals(80, response.getGlobalScore());
        assertEquals("Me sentí bastante bien hoy.", response.getReflection());

        verify(dailyEvaluationRepository).findByUserIdAndDate(userId, date);
        verify(dailyEvaluationRepository).save(captor.capture());

        DailyEvaluation toSave = captor.getValue();
        assertEquals(userId, toSave.getUserId());
        assertEquals(date, toSave.getDate());
        assertEquals(MoodLabel.FELIZ, toSave.getMainMood());
        assertEquals(80, toSave.getGlobalScore());
        assertEquals("Me sentí bastante bien hoy.", toSave.getReflection());
    }

    @Test
    void upsertDailyEvaluation_actualizaCuandoExiste() {
        // given
        String userId = "user-123";
        LocalDate date = LocalDate.of(2025, 11, 23);

        DailyEvaluation existing = DailyEvaluation.builder()
                .id(5L)
                .userId(userId)
                .date(date)
                .mainMood(MoodLabel.TRISTE)
                .globalScore(40)
                .reflection("Día difícil")
                .build();

        DailyEvaluationRequest request = new DailyEvaluationRequest();
        request.setDate(date);
        request.setMoodLabel("FELIZ");
        request.setGlobalScore(90);
        request.setReflection("Al final del día me sentí mejor.");

        when(dailyEvaluationRepository.findByUserIdAndDate(userId, date))
                .thenReturn(Optional.of(existing));

        when(dailyEvaluationRepository.save(existing))
                .thenReturn(existing);

        // when
        DailyEvaluationResponse response =
                dailyEvaluationService.upsertDailyEvaluation(userId, request);

        // then
        assertNotNull(response);
        assertEquals(5L, response.getId());
        assertEquals("FELIZ", response.getMoodLabel());
        assertEquals(90, response.getGlobalScore());
        assertEquals("Al final del día me sentí mejor.", response.getReflection());

        verify(dailyEvaluationRepository).findByUserIdAndDate(userId, date);
        verify(dailyEvaluationRepository).save(existing);
    }

    @Test
    void getByDate_devuelveRespuestaCuandoExiste() {
        // given
        String userId = "user-123";
        LocalDate date = LocalDate.of(2025, 11, 23);

        DailyEvaluation entity = DailyEvaluation.builder()
                .id(2L)
                .userId(userId)
                .date(date)
                .mainMood(MoodLabel.ANSIOSO)
                .globalScore(60)
                .reflection("Me sentí nervioso.")
                .build();

        when(dailyEvaluationRepository.findByUserIdAndDate(userId, date))
                .thenReturn(Optional.of(entity));

        // when
        DailyEvaluationResponse response =
                dailyEvaluationService.getByDate(userId, date);

        // then
        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("ANSIOSO", response.getMoodLabel());
        assertEquals(60, response.getGlobalScore());
        assertEquals("Me sentí nervioso.", response.getReflection());

        verify(dailyEvaluationRepository).findByUserIdAndDate(userId, date);
    }

    @Test
    void getByDate_lanzaExcepcionCuandoNoExiste() {
        // given
        String userId = "user-123";
        LocalDate date = LocalDate.of(2025, 11, 23);

        when(dailyEvaluationRepository.findByUserIdAndDate(userId, date))
                .thenReturn(Optional.empty());

        // then
        assertThrows(IllegalArgumentException.class, () ->
                dailyEvaluationService.getByDate(userId, date)
        );

        verify(dailyEvaluationRepository).findByUserIdAndDate(userId, date);
    }

    @Test
    void getHistory_mapeaListaCorrectamente() {
        // given
        String userId = "user-123";

        DailyEvaluation e1 = DailyEvaluation.builder()
                .id(1L).userId(userId)
                .date(LocalDate.of(2025, 11, 20))
                .mainMood(MoodLabel.FELIZ)
                .globalScore(90)
                .reflection("Buen día")
                .build();

        DailyEvaluation e2 = DailyEvaluation.builder()
                .id(2L).userId(userId)
                .date(LocalDate.of(2025, 11, 21))
                .mainMood(MoodLabel.TRISTE)
                .globalScore(30)
                .reflection("Día difícil")
                .build();

        when(dailyEvaluationRepository.findByUserIdOrderByDateDesc(userId))
                .thenReturn(List.of(e2, e1));

        // when
        List<DailyEvaluationResponse> result =
                dailyEvaluationService.getHistory(userId);

        // then
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(1L, result.get(1).getId());

        verify(dailyEvaluationRepository).findByUserIdOrderByDateDesc(userId);
    }
}
