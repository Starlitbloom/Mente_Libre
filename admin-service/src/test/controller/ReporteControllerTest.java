package com.mentelibre.admin_service.controller;

import com.mentelibre.admin_service.model.ReporteGeneral;
import com.mentelibre.admin_service.service.ReporteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = ReporteController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class} // deshabilita seguridad
)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReporteGeneral reporte;

    @BeforeEach
    void setUp() {
        reporte = new ReporteGeneral(10, 2, Map.of(
                "Goals Service", 5,
                "Evaluation Service", 3
        ));
    }

    @Test
    void testGenerarReporte() throws Exception {
        when(reporteService.generarReporte()).thenReturn(reporte);

        mockMvc.perform(post("/api/admin/reportes/generar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsuarios").value(10))
                .andExpect(jsonPath("$.usuariosBloqueados").value(2));

        verify(reporteService, times(1)).generarReporte();
    }

    @Test
    void testListarReportes() throws Exception {
        when(reporteService.listarReportes()).thenReturn(List.of(reporte));

        mockMvc.perform(get("/api/admin/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].totalUsuarios").value(10));

        verify(reporteService, times(1)).listarReportes();
    }
}
