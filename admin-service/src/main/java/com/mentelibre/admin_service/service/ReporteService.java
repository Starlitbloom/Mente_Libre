package com.mentelibre.admin_service.service;

import com.mentelibre.admin_service.model.ReporteGeneral;
import com.mentelibre.admin_service.repository.ReporteGeneralRepository;
import com.mentelibre.admin_service.repository.UserAdminRepository;
import com.mentelibre.admin_service.webclient.EmotionClient;
import com.mentelibre.admin_service.webclient.EvaluationClient;
import com.mentelibre.admin_service.webclient.UserClient;
import com.mentelibre.admin_service.webclient.GoalsClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    private final UserAdminRepository userAdminRepository;
    private final ReporteGeneralRepository reporteGeneralRepository;
    private final UserClient userClient;
    private final EvaluationClient evaluationClient;
    private final GoalsClient goalsClient;
    private final EmotionClient emotionClient;

    @Autowired
    public ReporteService(UserAdminRepository userAdminRepository,
                          ReporteGeneralRepository reporteGeneralRepository,
                          UserClient userClient,
                          EvaluationClient evaluationClient,
                          GoalsClient goalsClient,
                          EmotionClient emotionClient) {

        this.userAdminRepository = userAdminRepository;
        this.reporteGeneralRepository = reporteGeneralRepository;
        this.userClient = userClient;
        this.evaluationClient = evaluationClient;
        this.goalsClient = goalsClient;
        this.emotionClient = emotionClient;
    }

    public ReporteGeneral generarReporte(String token) {

        // ----------- USER SERVICE -----------
        int totalUsuarios = userClient.getAllProfiles(token).size();

        // ----------- ADMIN SERVICE -----------
        int usuariosBloqueados = userAdminRepository.countByBloqueadoTrue();

        // ----------- EVALUATION SERVICE -----------
        int totalEvaluaciones = evaluationClient.getAllEvaluations(token).size();

        // ----------- GOALS SERVICE -----------
        int totalMetas = goalsClient.getAllGoals(token).size();
        int totalLogros = goalsClient.getAllAchievements(token).size();
        int totalProgresos = goalsClient.getAllProgress(token).size();

        // ----------- EMOTION SERVICE -----------
        int totalEmociones = emotionClient.getAllEmotions(token).size();

        // ----------- MAPA DE OBJETIVOS POR SERVICIO -----------
        Map<String, Integer> objetivosPorServicio = new HashMap<>();
        objetivosPorServicio.put("Evaluation Service - Evaluaciones", totalEvaluaciones);
        objetivosPorServicio.put("Goals Service - Metas", totalMetas);
        objetivosPorServicio.put("Goals Service - Logros", totalLogros);
        objetivosPorServicio.put("Goals Service - Progresos", totalProgresos);
        objetivosPorServicio.put("Emotion Service - Emociones Registradas", totalEmociones);

        // ----------- Crear y guardar reporte -----------
        ReporteGeneral reporte = new ReporteGeneral(
            null,
            totalUsuarios,
            usuariosBloqueados,
            objetivosPorServicio,
            null
        );

        return reporteGeneralRepository.save(reporte);
    }

    public List<ReporteGeneral> listarReportes() {
        return reporteGeneralRepository.findAll();
    }
}

