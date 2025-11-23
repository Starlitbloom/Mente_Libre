// src/test/java/com/mentelibre/emotion_service/controller/MoodRecordControllerTest.java
package com.mentelibre.emotion_service.controller;

import com.mentelibre.emotion_service.model.MoodRecord;
import com.mentelibre.emotion_service.service.MoodRecordService;
import com.mentelibre.emotion_service.service.MoodRecordService.ScoreSummary;
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
class MoodRecordControllerTest {

    @Mock
    private MoodRecordService moodRecordService;

    @InjectMocks
    private MoodRecordController moodRecordController;

    @Test
    void register_delegaEnServiceYDevuelveMoodRecord() {
        String userId = "user-1";
        String emotionLabel = "Feliz";
        String context = "Día bonito";
        String dateStr = "2025-01-10";
        LocalDate date = LocalDate.parse(dateStr);

        MoodRecord saved = new MoodRecord();
        saved.setUserId(userId);
        saved.setRecordDate(date);
        saved.setEmotionLabel(emotionLabel);
        saved.setEmotionScore(100);
        saved.setContext(context);

        when(moodRecordService.registerOrUpdateMood(userId, emotionLabel, context, date))
                .thenReturn(saved);

        MoodRecord result = moodRecordController.register(userId, emotionLabel, context, dateStr);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(date, result.getRecordDate());
        assertEquals(emotionLabel, result.getEmotionLabel());
        assertEquals(context, result.getContext());

        verify(moodRecordService, times(1))
                .registerOrUpdateMood(userId, emotionLabel, context, date);
    }

    @Test
    void getRange_devuelveListaDeRegistros() {
        String userId = "user-2";
        String startStr = "2025-01-01";
        String endStr   = "2025-01-31";
        LocalDate start = LocalDate.parse(startStr);
        LocalDate end   = LocalDate.parse(endStr);

        MoodRecord r1 = new MoodRecord();
        r1.setUserId(userId);
        r1.setRecordDate(LocalDate.of(2025, 1, 5));
        r1.setEmotionLabel("Feliz");
        r1.setEmotionScore(100);
        r1.setContext("Contexto 1");

        MoodRecord r2 = new MoodRecord();
        r2.setUserId(userId);
        r2.setRecordDate(LocalDate.of(2025, 1, 10));
        r2.setEmotionLabel("Triste");
        r2.setEmotionScore(35);
        r2.setContext("Contexto 2");

        when(moodRecordService.getRecordsForRange(userId, start, end))
                .thenReturn(List.of(r1, r2));

        List<MoodRecord> result =
                moodRecordController.getRange(userId, startStr, endStr);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(r1.getRecordDate(),   result.get(0).getRecordDate());
        assertEquals(r1.getEmotionLabel(), result.get(0).getEmotionLabel());
        assertEquals(r2.getRecordDate(),   result.get(1).getRecordDate());
        assertEquals(r2.getEmotionLabel(), result.get(1).getEmotionLabel());

        verify(moodRecordService, times(1))
                .getRecordsForRange(userId, start, end);
    }

    @Test
    void getLastDays_devuelveScoreSummary() {
        String userId = "user-3";
        int days = 30;

        MoodRecord r = new MoodRecord();
        r.setUserId(userId);
        r.setRecordDate(LocalDate.now());
        r.setEmotionLabel("Feliz");
        r.setEmotionScore(100);
        r.setContext("Bien");

        ScoreSummary summary = new ScoreSummary(
                90,
                "Muy bien 😊 Estás mentalmente saludable",
                List.of(r)
        );

        when(moodRecordService.getScoreSummaryForLastDays(userId, days))
                .thenReturn(summary);

        MoodRecordService.ScoreSummary result =
                moodRecordController.getLastDays(userId, days);

        assertNotNull(result);
        assertEquals(90, result.getAverageScore());
        assertEquals("Muy bien 😊 Estás mentalmente saludable", result.getHealthMessage());
        assertEquals(1, result.getRecords().size());

        verify(moodRecordService, times(1))
                .getScoreSummaryForLastDays(userId, days);
    }

    @Test
    void getMonth_devuelveRegistrosDelMes() {
        String userId = "user-4";
        int year = 2025;
        int month = 1;

        MoodRecord r = new MoodRecord();
        r.setUserId(userId);
        r.setRecordDate(LocalDate.of(2025, 1, 15));
        r.setEmotionLabel("Neutral");
        r.setEmotionScore(60);

        when(moodRecordService.getRecordsForMonth(userId, year, month))
                .thenReturn(List.of(r));

        List<MoodRecord> result =
                moodRecordController.getMonth(userId, year, month);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2025, 1, 15), result.get(0).getRecordDate());
        assertEquals("Neutral", result.get(0).getEmotionLabel());

        verify(moodRecordService, times(1))
                .getRecordsForMonth(userId, year, month);
    }

    @Test
    void getYear_devuelveRegistrosDelAnio() {
        String userId = "user-5";
        int year = 2025;

        MoodRecord r = new MoodRecord();
        r.setUserId(userId);
        r.setRecordDate(LocalDate.of(2025, 3, 10));
        r.setEmotionLabel("Tranquilo");
        r.setEmotionScore(85);

        when(moodRecordService.getRecordsForYear(userId, year))
                .thenReturn(List.of(r));

        List<MoodRecord> result =
                moodRecordController.getYear(userId, year);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2025, 3, 10), result.get(0).getRecordDate());
        assertEquals("Tranquilo", result.get(0).getEmotionLabel());

        verify(moodRecordService, times(1))
                .getRecordsForYear(userId, year);
    }
}
