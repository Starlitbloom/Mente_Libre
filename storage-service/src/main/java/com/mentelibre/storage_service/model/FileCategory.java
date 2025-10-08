package com.mentelibre.storage_service.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Categoría del archivo subido")
public enum FileCategory {
    PROFILE,    // Foto de perfil del estudiante
    GRATITUDE   // Fotos en diario de gratitud
}

