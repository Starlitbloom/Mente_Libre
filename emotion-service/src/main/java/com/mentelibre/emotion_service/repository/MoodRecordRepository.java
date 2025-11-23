package com.mentelibre.emotion_service.repository;

import com.mentelibre.emotion_service.model.MoodRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MoodRecordRepository extends JpaRepository<MoodRecord, Long> {

    /**
     * Devuelve todos los registros de un usuario entre dos fechas (inclusive),
     * ordenados cronológicamente de forma ASCENDENTE.
     *
     * Se usa para:
     *  - filtros de día / semana / mes / año en Android
     *  - generación de PDF con el historial
     */
    List<MoodRecord> findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
            String userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Devuelve el historial completo de un usuario ordenado de más nuevo a más antiguo.
     * (útil si después quieres una pantalla tipo "historial reciente").
     */
    List<MoodRecord> findByUserIdOrderByRecordDateDesc(String userId);
}
