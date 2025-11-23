package com.mentelibre.evaluation_service.controller;

import com.mentelibre.evaluation_service.dto.DailyEvaluationRequest;
import com.mentelibre.evaluation_service.dto.DailyEvaluationResponse;
import com.mentelibre.evaluation_service.dto.GratitudeEntryRequest;
import com.mentelibre.evaluation_service.dto.GratitudeEntryResponse;
import com.mentelibre.evaluation_service.service.DailyEvaluationService;
import com.mentelibre.evaluation_service.service.GratitudeEntryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationControllerTest {

    @Mock
    private DailyEvaluationService dailyEvaluationService;

    @Mock
    private GratitudeEntryService gratitudeEntryService;

    @InjectMocks
    private EvaluationController evaluationController;

    @Test
    void upsertDailyEvaluation_devuelveRespuestaDelService() {
        // given
        String userId = "user-123";
        DailyEvaluationRequest request = new DailyEvaluationRequest();
        request.setDate(LocalDate.of(2025, 11, 23));
        request.setMoodLabel("FELIZ");
        request.setGlobalScore(85);
        request.setReflection("Día productivo.");

        DailyEvaluationResponse expected = DailyEvaluationResponse.builder()
                .id(1L)
                .userId(userId)
                .date(request.getDate())
                .moodLabel("FELIZ")
                .globalScore(85)
                .reflection("Día productivo.")
                .build();

        when(dailyEvaluationService.upsertDailyEvaluation(userId, request))
                .thenReturn(expected);

        // when
        DailyEvaluationResponse response =
                evaluationController.upsertDailyEvaluation(userId, request);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(userId, response.getUserId());
        assertEquals("FELIZ", response.getMoodLabel());

        verify(dailyEvaluationService).upsertDailyEvaluation(userId, request);
    }

    @Test
    void getDailyEvaluation_llamaServiceYDevuelveRespuesta() {
        // given
        String userId = "user-123";
        LocalDate date = LocalDate.of(2025, 11, 23);

        DailyEvaluationResponse expected = DailyEvaluationResponse.builder()
                .id(2L)
                .userId(userId)
                .date(date)
                .moodLabel("TRISTE")
                .globalScore(40)
                .build();

        when(dailyEvaluationService.getByDate(userId, date))
                .thenReturn(expected);

        // when
        DailyEvaluationResponse response =
                evaluationController.getDailyEvaluation(userId, date);

        // then
        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("TRISTE", response.getMoodLabel());

        verify(dailyEvaluationService).getByDate(userId, date);
    }

    @Test
    void getHistory_devuelveListaDesdeService() {
        // given
        String userId = "user-123";

        DailyEvaluationResponse r1 = DailyEvaluationResponse.builder()
                .id(1L).userId(userId)
                .date(LocalDate.of(2025, 11, 20))
                .build();

        DailyEvaluationResponse r2 = DailyEvaluationResponse.builder()
                .id(2L).userId(userId)
                .date(LocalDate.of(2025, 11, 21))
                .build();

        when(dailyEvaluationService.getHistory(userId))
                .thenReturn(List.of(r1, r2));

        // when
        List<DailyEvaluationResponse> list =
                evaluationController.getHistory(userId);

        // then
        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(2L, list.get(1).getId());

        verify(dailyEvaluationService).getHistory(userId);
    }

    @Test
    void createGratitudeEntry_devuelveRespuestaDelService() {
        // given
        String userId = "user-123";

        GratitudeEntryRequest request = new GratitudeEntryRequest();
        request.setText("Gracias por el apoyo de mis amigos.");

        GratitudeEntryResponse expected = GratitudeEntryResponse.builder()
                .id(10L)
                .userId(userId)
                .text("Gracias por el apoyo de mis amigos.")
                .build();

        when(gratitudeEntryService.createEntry(userId, request))
                .thenReturn(expected);

        // when
        GratitudeEntryResponse response =
                evaluationController.createGratitudeEntry(userId, request);

        // then
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(userId, response.getUserId());
        assertEquals("Gracias por el apoyo de mis amigos.", response.getText());

        verify(gratitudeEntryService).createEntry(userId, request);
    }

    @Test
    void getGratitudeEntries_devuelveListaDesdeService() {
        // given
        String userId = "user-123";

        GratitudeEntryResponse g1 = GratitudeEntryResponse.builder()
                .id(1L)
                .userId(userId)
                .text("Texto 1")
                .build();

        GratitudeEntryResponse g2 = GratitudeEntryResponse.builder()
                .id(2L)
                .userId(userId)
                .text("Texto 2")
                .build();

        when(gratitudeEntryService.getAllForUser(userId))
                .thenReturn(List.of(g1, g2));

        // when
        List<GratitudeEntryResponse> list =
                evaluationController.getGratitudeEntries(userId);

        // then
        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(2L, list.get(1).getId());

        verify(gratitudeEntryService).getAllForUser(userId);
    }

    @Test
    void getGratitudeEntriesForDay_devuelveListaDesdeService() {
        // given
        String userId = "user-123";
        LocalDate date = LocalDate.of(2025, 11, 23);

        GratitudeEntryResponse g1 = GratitudeEntryResponse.builder()
                .id(1L)
                .userId(userId)
                .date(date)
                .text("Gracias por dormir bien.")
                .build();

        when(gratitudeEntryService.getForDay(userId, date))
                .thenReturn(List.of(g1));

        // when
        List<GratitudeEntryResponse> list =
                evaluationController.getGratitudeEntriesForDay(userId, date);

        // then
        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals("Gracias por dormir bien.", list.get(0).getText());

        verify(gratitudeEntryService).getForDay(userId, date);
    }
}
