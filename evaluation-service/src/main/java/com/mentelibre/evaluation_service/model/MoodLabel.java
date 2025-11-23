package com.mentelibre.evaluation_service.model;

/**
 * Estados de ánimo disponibles en la app.
 * Puedes agregar o quitar según tu enum de Android.
 */
public enum MoodLabel {
    FELIZ("Feliz"),
    TRANQUILO("Tranquilo"),
    SERENO("Sereno"),
    NEUTRAL("Neutral"),
    ENOJADO("Enojado"),
    TRISTE("Triste"),
    DEPRIMIDO("Deprimido"),
    ANSIOSO("Ansioso");

    private final String label;
    MoodLabel(String label) { this.label = label; }
    public String getLabel() { return label; }
}

