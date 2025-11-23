package com.mentelibre.emotion_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;

import java.util.function.Function;

/**
 * Utilidad para validar y leer datos del JWT.
 * Debe usar la MISMA clave secreta que tu auth-service.
 */
public class JwtUtil {

    // 👉 Usa exactamente la misma clave que en tu auth-service
    private static final String SECRET_KEY = "CAMBIA_ESTA_CLAVE_POR_LA_DEL_AUTH_SERVICE";

    /**
     * Extrae el "subject" (normalmente el username o userId) del token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae un claim genérico usando una función.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parsea el token y devuelve todos los claims.
     */
    private Claims extractAllClaims(String token) throws JwtException {
        return Jwts
                .parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Valida que el token sea correcto (firma y formato).
     */
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
