package com.mentelibre.admin_service.service;

import com.mentelibre.admin_service.model.ReporteGeneral;
import com.mentelibre.admin_service.repository.ReporteGeneralRepository;
import com.mentelibre.admin_service.repository.UserAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    @Autowired
    private UserAdminRepository userAdminRepository;

    @Autowired
    private ReporteGeneralRepository reporteGeneralRepository;

    // Generar reporte general del sistema
    public ReporteGeneral generarReporte() {
        int totalUsuarios = (int) userAdminRepository.count();
        int usuariosBloqueados = userAdminRepository.countByBloqueadoTrue();

        // Ejemplo: agregar métricas de microservicios (Goals, Evaluation, Emotion)
        Map<String, Integer> objetivosPorServicio = new HashMap<>();
        objetivosPorServicio.put("Goals Service", 10);
        objetivosPorServicio.put("Evaluation Service", 5);
        objetivosPorServicio.put("Emotion Service", 7);

        ReporteGeneral reporte = new ReporteGeneral(totalUsuarios, usuariosBloqueados, objetivosPorServicio);

        // Guardar reporte en BD
        return reporteGeneralRepository.save(reporte);
    }

    // Listar reportes históricos
    public List<ReporteGeneral> listarReportes() {
        return reporteGeneralRepository.findAll();
    }
}


