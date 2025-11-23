package com.mentelibre.emotion_service.service;

import com.mentelibre.emotion_service.model.MoodRecord;
import com.mentelibre.emotion_service.repository.MoodRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Servicio de dominio para manejar los registros de ánimo (MoodRecord).
 *
 * La idea es que el backend tenga la misma lógica que tu app Android:
 * - Registrar o actualizar el ánimo del día.
 * - Calcular el puntaje promedio de los últimos N días (pantalla Puntaje).
 * - Entregar listas por rango (día, semana, mes, año) para gráficos / PDF.
 */
@Service
@RequiredArgsConstructor
public class MoodRecordService {

    private final MoodRecordRepository moodRecordRepository;

    // =========================================================
    // 1) REGISTRO / ACTUALIZACIÓN DEL ÁNIMO DE UN DÍA
    // =========================================================

    /**
     * Registra o actualiza el estado de ánimo para un usuario en una fecha dada.
     *
     * Igual que en Android:
     *  - Si ya hay registro para "hoy" -> se reemplaza el ánimo.
     *  - Si no hay -> se inserta uno nuevo.
     *
     * @param userId        id del usuario (String, viene de tu auth-service)
     * @param emotionLabel  etiqueta de la emoción ("Feliz", "Triste", etc.)
     * @param context       contexto opcional (por qué te sientes así)
     * @param recordDate    fecha a registrar (normalmente LocalDate.now())
     * @return MoodRecord guardado (nuevo o actualizado)
     */
    @Transactional
    public MoodRecord registerOrUpdateMood(String userId,
                                           String emotionLabel,
                                           String context,
                                           LocalDate recordDate) {

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId no puede ser null ni vacío");
        }
        if (recordDate == null) {
            recordDate = LocalDate.now();
        }

        Integer score = mapLabelToScore(emotionLabel);

        // Buscamos si ya existe registro para ese usuario + fecha.
        // Reutilizamos el método por rango usando start = end = recordDate.
        List<MoodRecord> sameDayRecords =
                moodRecordRepository.findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
                        userId, recordDate, recordDate
                );

        MoodRecord toSave;
        if (sameDayRecords.isEmpty()) {
            // No hay registro -> creamos uno nuevo
            toSave = new MoodRecord();
            toSave.setUserId(userId);
            toSave.setRecordDate(recordDate);
        } else {
            // Ya existe -> actualizamos el primero
            toSave = sameDayRecords.get(0);
        }

        toSave.setEmotionLabel(emotionLabel);
        toSave.setEmotionScore(score);
        toSave.setContext(context);

        return moodRecordRepository.save(toSave);
    }

    // =========================================================
    // 2) OBTENER REGISTROS POR RANGO (DÍA / SEMANA / MES / AÑO)
    // =========================================================

    /**
     * Devuelve todos los registros de un usuario entre dos fechas (inclusive),
     * ordenados por fecha ascendente.
     *
     * Lo usarás para:
     * - Puntaje de los últimos 30 días.
     * - Gráfico día/semana/mes/año.
     * - Datos para PDF.
     */
    public List<MoodRecord> getRecordsForRange(String userId,
                                               LocalDate startDate,
                                               LocalDate endDate) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId no puede ser null ni vacío");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate y endDate no pueden ser null");
        }
        if (endDate.isBefore(startDate)) {
            // Si vienen invertidas, las corregimos
            LocalDate tmp = startDate;
            startDate = endDate;
            endDate = tmp;
        }

        return moodRecordRepository
                .findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(userId, startDate, endDate);
    }

    /**
     * Registros de los últimos N días (incluyendo hoy).
     * Igual que en tu pantalla Puntaje (últimos 30 días).
     *
     * @param days número de días hacia atrás (ej: 30)
     */
    public List<MoodRecord> getRecordsForLastDays(String userId, int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("days debe ser > 0");
        }
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days - 1L);
        return getRecordsForRange(userId, start, end);
    }

    /**
     * Registros de un mes específico (para gráfico y PDF mensual).
     *
     * @param year  año (ej: 2025)
     * @param month mes 1-12 (ej: 1 = enero)
     */
    public List<MoodRecord> getRecordsForMonth(String userId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        return getRecordsForRange(userId, start, end);
    }

    /**
     * Registros de todo un año (para gráfico y PDF anual).
     */
    public List<MoodRecord> getRecordsForYear(String userId, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        return getRecordsForRange(userId, start, end);
    }

    // =========================================================
    // 3) CÁLCULO DE PUNTAJE (PANTALLA PUNTAJE EN ANDROID)
    // =========================================================

    /**
     * Calcula el puntaje promedio de los últimos N días para un usuario.
     *
     * Es la misma lógica de tu pantalla Puntaje:
     *   - Se mapea cada emoción a un número.
     *   - Se promedia.
     *   - Si no hay datos, 0.
     *
     * @param days número de días, por defecto en Android usas 30.
     * @return puntaje promedio redondeado (0..100)
     */
    public int getAverageScoreForLastDays(String userId, int days) {
        List<MoodRecord> records = getRecordsForLastDays(userId, days);
        return calculateAverageScore(records);
    }

    /**
     * Devuelve un resumen completo para la pantalla Puntaje:
     * - puntaje promedio
     * - texto de bienestar mental
     * - lista de registros usados
     */
    public ScoreSummary getScoreSummaryForLastDays(String userId, int days) {
        List<MoodRecord> records = getRecordsForLastDays(userId, days);
        int avg = calculateAverageScore(records);
        String msg = buildHealthMessage(avg);
        return new ScoreSummary(avg, msg, records);
    }

    // =========================================================
    // 4) HELPERS: MAPEO DE EMOCIÓN → PUNTAJE Y MENSAJE
    // =========================================================

    /**
     * Mapea la etiqueta de emoción (texto que llega de Android)
     * a un puntaje numérico igual que en tu Pantalla Puntaje.
     *
     * Si el label no coincide, retorna 0.
     */
    private Integer mapLabelToScore(String label) {
        if (label == null) return 0;
        return switch (label.trim()) {
            case "Feliz"      -> 100;
            case "Tranquilo"  -> 85;
            case "Sereno"     -> 75;
            case "Neutral"    -> 60;
            case "Enojado"    -> 40;
            case "Triste"     -> 35;
            case "Deprimido"  -> 25;
            default           -> 0;
        };
    }

    /**
     * Calcula el promedio de la columna emotionScore de una lista de registros.
     * Si la lista está vacía, devuelve 0.
     */
    private int calculateAverageScore(List<MoodRecord> records) {
        if (records == null || records.isEmpty()) {
            return 0;
        }
        return (int) Math.round(
                records.stream()
                        .map(MoodRecord::getEmotionScore)
                        .filter(s -> s != null)
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0.0)
        );
    }

    /**
     * Crea el mensaje de bienestar mental según el puntaje,
     * igual que en tu código de Android (PuntajeScreen).
     */
    private String buildHealthMessage(int percentage) {
        if (percentage >= 90) {
            return "¡Excelente! Tu bienestar mental está en su punto más alto ✨";
        } else if (percentage >= 75) {
            return "Muy bien 😊 Estás mentalmente saludable";
        } else if (percentage >= 60) {
            return "Bien 👍 Mantén el equilibrio y cuida de ti";
        } else if (percentage >= 40) {
            return "Atención ⚠️ podrías estar algo estresado, tómate un respiro";
        } else {
            return "Cuidado 💭 sería recomendable hablar con un especialista";
        }
    }

    // =========================================================
    // 5) DTO interno para devolver resumen de puntaje
    // =========================================================

    /**
     * DTO simple para exponer el resumen de puntaje.
     * Lo puede usar directamente el controller para la respuesta JSON.
     */
    public static class ScoreSummary {
        private final int averageScore;
        private final String healthMessage;
        private final List<MoodRecord> records;

        public ScoreSummary(int averageScore, String healthMessage, List<MoodRecord> records) {
            this.averageScore = averageScore;
            this.healthMessage = healthMessage;
            this.records = records;
        }

        public int getAverageScore() {
            return averageScore;
        }

        public String getHealthMessage() {
            return healthMessage;
        }

        public List<MoodRecord> getRecords() {
            return records;
        }
    }
}
