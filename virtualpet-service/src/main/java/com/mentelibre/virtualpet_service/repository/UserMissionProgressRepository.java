package com.mentelibre.virtualpet_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.virtualpet_service.model.UserMissionProgress;

@Repository
public interface UserMissionProgressRepository extends JpaRepository<UserMissionProgress, Long> {

    // Progreso de un usuario en una misión
    UserMissionProgress findByUserIdAndMissionId(Long userId, Long missionId);

    // Todas las misiones que está haciendo un usuario
    List<UserMissionProgress> findByUserId(Long userId);

    // Revisar si ya completó una misión ese día
    boolean existsByUserIdAndMissionId(Long userId, Long missionId);
}