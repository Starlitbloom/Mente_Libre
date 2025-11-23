package com.mentelibre.evaluation_service.service;

import com.mentelibre.evaluation_service.dto.GratitudeEntryRequest;
import com.mentelibre.evaluation_service.dto.GratitudeEntryResponse;
import com.mentelibre.evaluation_service.model.DailyEvaluation;
import com.mentelibre.evaluation_service.model.GratitudeEntry;
import com.mentelibre.evaluation_service.model.MoodLabel;
import com.mentelibre.evaluation_service.repository.DailyEvaluationRepository;
import com.mentelibre.evaluation_service.repository.GratitudeEntryRepository;
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
class GratitudeEntryServiceTest {

    @Mock
    private GratitudeEntryRepository gratitudeEntryRepository;

    @Mock
    private DailyEvaluationRepository dailyEvaluationRepository;

    @InjectMocks
    private GratitudeEntryService gratitudeEntryService;

    @Test
    void createEntry_creaEntradaSinDailyEvaluation() {
        // given
        String userId = "user-123";
        LocalDate date = LocalDate.of(2025, 11, 23);

        GratitudeEntryRequest request = new GratitudeEntryRequest();
        request.setDate(date);
        request.setMoodLabel("FELIZ");
        request.setTitle("Pequeñas cosas");
        request.setText("Hoy agradezco por el café de la mañana.");

        when(dailyEvaluationRepository.findById(anyLong()))
                .thenReturn(Optional.empty()); // no deberían llamar esto si dailyEvaluationId es null

        ArgumentCaptor<GratitudeEntry> captor = ArgumentCaptor.forClass(GratitudeEntry.class);

        GratitudeEntry savedEntity = GratitudeEntry.builder()
                .id(10L)
                .userId(userId)
                .date(date)
                .moodLabel(MoodLabel.FELIZ)
                .title("Pequeñas cosas")
                .text("Hoy agradezco por el café de la mañana.")
                .build();

        when(gratitudeEntryRepository.save(any(GratitudeEntry.class)))
                .thenReturn(savedEntity);

        // when
        GratitudeEntryResponse response =
                gratitudeEntryService.createEntry(userId, request);

        // then
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(userId, response.getUserId());
        assertEquals(date, response.getDate());
        assertEquals("FELIZ", response.getMoodLabel());
        assertEquals("Pequeñas cosas", response.getTitle());
        assertEquals("Hoy agradezco por el café de la mañana.", response.getText());
        assertNull(response.getDailyEvaluationId());

        verify(gratitudeEntryRepository).save(captor.capture());
        GratitudeEntry toSave = captor.getValue();
        assertEquals(userId, toSave.getUserId());
        assertEquals(date, toSave.getDate());
        assertEquals(MoodLabel.FELIZ, toSave.getMoodLabel());
        assertEquals("Pequeñas cosas", toSave.getTitle());
        assertEquals("Hoy agradezco por el café de la mañana.", toSave.getText());

        verifyNoInteractions(dailyEvaluationRepository);
    }

    @Test
    void createEntry_conDailyEvaluationNoExistente_lanzaExcepcion() {
        // given
        String userId = "user-123";

        GratitudeEntryRequest request = new GratitudeEntryRequest();
        request.setText("Gracias por todo hoy.");
        request.setDailyEvaluationId(99L);

        when(dailyEvaluationRepository.findById(99L))
                .thenReturn(Optional.empty());

        // then
        assertThrows(IllegalArgumentException.class, () ->
                gratitudeEntryService.createEntry(userId, request)
        );

        verify(dailyEvaluationRepository).findById(99L);
        verifyNoInteractions(gratitudeEntryRepository);
    }

    @Test
    void createEntry_conTextoVacio_lanzaExcepcion() {
        // given
        String userId = "user-123";

        GratitudeEntryRequest request = new GratitudeEntryRequest();
        request.setText("   ");

        // then
        assertThrows(IllegalArgumentException.class, () ->
                gratitudeEntryService.createEntry(userId, request)
        );

        verifyNoInteractions(gratitudeEntryRepository);
        verifyNoInteractions(dailyEvaluationRepository);
    }

    @Test
    void getAllForUser_mapeaListaCorrectamente() {
        // given
        String userId = "user-123";

        GratitudeEntry e1 = GratitudeEntry.builder()
                .id(1L)
                .userId(userId)
                .date(LocalDate.of(2025, 11, 20))
                .moodLabel(MoodLabel.NEUTRAL)
                .title("Título 1")
                .text("Texto 1")
                .build();

        GratitudeEntry e2 = GratitudeEntry.builder()
                .id(2L)
                .userId(userId)
                .date(LocalDate.of(2025, 11, 21))
                .moodLabel(MoodLabel.FELIZ)
                .title("Título 2")
                .text("Texto 2")
                .build();

        when(gratitudeEntryRepository.findByUserIdOrderByDateAsc(userId))
                .thenReturn(List.of(e1, e2));

        // when
        List<GratitudeEntryResponse> list =
                gratitudeEntryService.getAllForUser(userId);

        // then
        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(2L, list.get(1).getId());

        verify(gratitudeEntryRepository).findByUserIdOrderByDateAsc(userId);
    }

    @Test
    void getForDay_mapeaListaCorrectamente() {
        // given
        String userId = "user-123";
        LocalDate date = LocalDate.of(2025, 11, 23);

        GratitudeEntry e1 = GratitudeEntry.builder()
                .id(1L)
                .userId(userId)
                .date(date)
                .moodLabel(MoodLabel.FELIZ)
                .title("Gracias")
                .text("Por la comida rica.")
                .build();

        when(gratitudeEntryRepository.findByUserIdAndDateOrderByCreatedAtAsc(userId, date))
                .thenReturn(List.of(e1));

        // when
        List<GratitudeEntryResponse> list =
                gratitudeEntryService.getForDay(userId, date);

        // then
        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals("FELIZ", list.get(0).getMoodLabel());

        verify(gratitudeEntryRepository)
                .findByUserIdAndDateOrderByCreatedAtAsc(userId, date);
    }
}
