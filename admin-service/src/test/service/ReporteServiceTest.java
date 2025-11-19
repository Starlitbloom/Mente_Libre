package com.mentelibre.admin_service.service;

import com.mentelibre.admin_service.model.ReporteGeneral;
import com.mentelibre.admin_service.repository.ReporteGeneralRepository;
import com.mentelibre.admin_service.repository.UserAdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReporteServiceTest {

    private ReporteService reporteService;
    private UserAdminRepository userAdminRepository;
    private ReporteGeneralRepository reporteGeneralRepository;

    @BeforeEach
    void setUp() {
        // Crear mocks
        userAdminRepository = mock(UserAdminRepository.class);
        reporteGeneralRepository = mock(ReporteGeneralRepository.class);

        // Inyectar mocks mediante el constructor
        reporteService = new ReporteService(userAdminRepository, reporteGeneralRepository);
    }

    @Test
    void testGenerarReporte() {
        // Configurar comportamiento de los mocks
        when(userAdminRepository.count()).thenReturn(10L);
        when(userAdminRepository.countByBloqueadoTrue()).thenReturn(2);

        ReporteGeneral esperado = new ReporteGeneral(10, 2, Map.of(
                "Goals Service", 10,
                "Evaluation Service", 5,
                "Emotion Service", 7
        ));

        when(reporteGeneralRepository.save(any(ReporteGeneral.class))).thenReturn(esperado);

        // Ejecutar método a testear
        ReporteGeneral result = reporteService.generarReporte();

        // Verificaciones
        assertEquals(10, result.getTotalUsuarios());
        assertEquals(2, result.getUsuariosBloqueados());
        assertTrue(result.getObjetivosPorServicio().containsKey("Goals Service"));

        verify(userAdminRepository, times(1)).count();
        verify(userAdminRepository, times(1)).countByBloqueadoTrue();
        verify(reporteGeneralRepository, times(1)).save(any(ReporteGeneral.class));
    }

    @Test
    void testListarReportes() {
        ReporteGeneral r = new ReporteGeneral(5, 1, Map.of("Goals Service", 2));
        when(reporteGeneralRepository.findAll()).thenReturn(List.of(r));

        List<ReporteGeneral> result = reporteService.listarReportes();

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getTotalUsuarios());
        verify(reporteGeneralRepository, times(1)).findAll();
    }
}
