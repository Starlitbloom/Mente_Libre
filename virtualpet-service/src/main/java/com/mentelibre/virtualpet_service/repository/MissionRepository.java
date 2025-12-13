package com.mentelibre.virtualpet_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentelibre.virtualpet_service.model.Mission;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    // Listar misiones por tipo (diaria, semanal, especial)
    List<Mission> findByType(String type);

    // Listar misiones activas
    List<Mission> findByActive(boolean active);
}