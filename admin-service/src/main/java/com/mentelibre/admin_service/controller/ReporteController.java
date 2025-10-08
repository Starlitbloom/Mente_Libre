package com.mentelibre.admin_service.controller;

import com.mentelibre.admin_service.model.ReporteGeneral;
import com.mentelibre.admin_service.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    // Generar y guardar reporte general
    @PostMapping("/generar")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReporteGeneral> generarReporte() {
        return ResponseEntity.ok(reporteService.generarReporte());
    }

    // Listar todos los reportes históricos
    @GetMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReporteGeneral>> listarReportes() {
        return ResponseEntity.ok(reporteService.listarReportes());
    }
}
