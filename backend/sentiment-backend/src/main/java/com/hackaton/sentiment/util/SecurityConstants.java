package com.hackaton.sentiment.util;

/**
 * Constantes de seguridad para la aplicación.
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
public class SecurityConstants {

    /**
     * Tiempo de expiración del token JWT (24 horas).
     */
    public static final long JWT_EXPIRATION = 86400000;

    /**
     * Clave secreta para firmar tokens JWT.
     *
     * <p>En producción, debe obtenerse de variables de entorno.</p>
     */
    public static final String JWT_SECRET =
            "Hackaton2024NoCountryOracleONESentimentAnalysisSecretKeyForJWTGeneration123";
}