// src/test/java/com/mentelibre/emotion_service/service/MoodRecordServiceTest.java
package com.mentelibre.emotion_service.service;

import com.mentelibre.emotion_service.model.MoodRecord;
import com.mentelibre.emotion_service.repository.MoodRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para la clase MoodRecordService.
 * 
 * Aquí se prueban:
 *  - La lógica de registro/actualización de estados de ánimo.
 *  - La obtención de registros por rango.
 *  - El cálculo del puntaje promedio.
 *
 * El repositorio se "mockea" (simula) porque:
 *  - NO queremos usar una base de datos real.
 *  - Para tener control total sobre lo que devuelve.
 *  - Para probar solamente la lógica del SERVICE.
 */
@ExtendWith(MockitoExtension.class)
class MoodRecordServiceTest {

    /**
     * Mock del repositorio.
     * Es decir: la BD "simulada" que sólo responde lo que nosotros le digamos.
     */
    @Mock
    private MoodRecordRepository moodRecordRepository;

    /**
     * Instancia real del servicio, pero con el repositorio mock inyectado.
     * Esto nos permite probar la lógica del servicio SIN tocar la BD real.
     */
    @InjectMocks
    private MoodRecordService moodRecordService;

    // ============================================================
    // TEST 1: registerOrUpdateMood
    // ============================================================

    /**
     * Caso de prueba:
     * Si el usuario NO tiene un registro previo en esa fecha,
     * el servicio debe crear uno nuevo.
     */
    @Test
    void registerOrUpdateMood_cuandoNoHayRegistro_creaNuevo() {

        // Datos de prueba
        String userId = "user-1";
        LocalDate date = LocalDate.of(2025, 1, 10);
        String label = "Feliz";
        String context = "Día bonito";

        // 1️⃣ Simulamos que NO hay registros para ese día:
        //    el repositorio devuelve lista vacía.
        when(moodRecordRepository
                .findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(userId, date, date))
                .thenReturn(Collections.emptyList());

        // 2️⃣ Creamos el objeto que queremos que "save()" devuelva.
        MoodRecord saved = new MoodRecord();
        saved.setUserId(userId);
        saved.setRecordDate(date);
        saved.setEmotionLabel(label);
        saved.setEmotionScore(100);   // mapeo de "Feliz"
        saved.setContext(context);

        // Indicamos qué debe devolver el método save()
        when(moodRecordRepository.save(any(MoodRecord.class))).thenReturn(saved);

        // 3️⃣ Ejecutamos el método real del SERVICE
        MoodRecord result = moodRecordService.registerOrUpdateMood(
                userId, label, context, date
        );

        // 4️⃣ Validaciones
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(date, result.getRecordDate());
        assertEquals(label, result.getEmotionLabel());
        assertEquals(100, result.getEmotionScore()); // puntaje asignado por el servicio
        assertEquals(context, result.getContext());

        // 5️⃣ Verificar que se llamó exactamente una vez a cada método esperado
        verify(moodRecordRepository, times(1))
                .findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(userId, date, date);

        verify(moodRecordRepository, times(1)).save(any(MoodRecord.class));
    }

    // ============================================================
    // TEST 2: getRecordsForLastDays
    // ============================================================

    /**
     * Prueba que el service calcule correctamente el rango de fechas
     * y llame al repositorio con esos valores.
     */
    @Test
    void getRecordsForLastDays_llamaAlRepositoryConRangoCorrecto() {

        String userId = "user-2";
        int days = 7;

        // Fechas que debe calcular internamente el service
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days - 1L);

        // Simulamos que el repositorio devuelve una lista vacía
        when(moodRecordRepository
                .findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(userId, start, end))
                .thenReturn(Collections.emptyList());

        // Ejecutar método real
        List<MoodRecord> result = moodRecordService.getRecordsForLastDays(userId, days);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verificar que el repo fue llamado con las fechas calculadas
        verify(moodRecordRepository, times(1))
                .findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(userId, start, end);
    }

    // ============================================================
    // TEST 3: getAverageScoreForLastDays
    // ============================================================

    /**
     * Prueba el cálculo del puntaje promedio para una lista de registros.
     * Se valida que:
     *  - Se obtengan los registros del repositorio.
     *  - Se promedien los puntajes correctamente.
     *  - Se redondee adecuadamente.
     */
    @Test
    void getAverageScoreForLastDays_calculaPromedio() {

        String userId = "user-3";
        int days = 3;

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days - 1L);

        // Creamos 3 registros con distintos puntajes
        MoodRecord r1 = new MoodRecord();
        r1.setUserId(userId);
        r1.setRecordDate(start);
        r1.setEmotionLabel("Feliz");
        r1.setEmotionScore(100);

        MoodRecord r2 = new MoodRecord();
        r2.setUserId(userId);
        r2.setRecordDate(start.plusDays(1));
        r2.setEmotionLabel("Tranquilo");
        r2.setEmotionScore(85);

        MoodRecord r3 = new MoodRecord();
        r3.setUserId(userId);
        r3.setRecordDate(end);
        r3.setEmotionLabel("Neutral");
        r3.setEmotionScore(60);

        // El repositorio devuelve estos 3 registros
        when(moodRecordRepository
                .findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(userId, start, end))
                .thenReturn(List.of(r1, r2, r3));

        // Ejecutar lógica real
        int avg = moodRecordService.getAverageScoreForLastDays(userId, days);

        // Promedio esperado:
        // (100 + 85 + 60) / 3 = 81.66 → redondeo → 82
        assertEquals(82, avg);

        verify(moodRecordRepository, times(1))
                .findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(userId, start, end);
    }
}
