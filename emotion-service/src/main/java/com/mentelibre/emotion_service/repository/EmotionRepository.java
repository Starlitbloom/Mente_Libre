package com.mentelibre.emotion_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mentelibre.emotion_service.model.Emotion;

@Repository
public interface EmotionRepository extends JpaRepository<Emotion, Long> {

    List<Emotion> findByUserId(Long userId);

    @Query("SELECT e FROM Emotion e WHERE e.userId = :userId AND e.fechaRegistro BETWEEN :start AND :end")
    List<Emotion> findByUserIdAndFechaBetween(Long userId, LocalDateTime start, LocalDateTime end);
}