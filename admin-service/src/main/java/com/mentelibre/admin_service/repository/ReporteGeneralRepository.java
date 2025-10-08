package com.mentelibre.admin_service.repository;

import com.mentelibre.admin_service.model.ReporteGeneral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReporteGeneralRepository extends JpaRepository<ReporteGeneral, Long> {

    // Ejemplo de método para filtrar reportes por rango de fechas
    List<ReporteGeneral> findByCreadoEnBetween(LocalDateTime inicio, LocalDateTime fin);

    // Puedes agregar más métodos personalizados según necesites estadísticas históricas
}
