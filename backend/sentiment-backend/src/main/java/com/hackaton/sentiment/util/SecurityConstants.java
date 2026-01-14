package com.hackaton.sentiment.util;

public class SecurityConstants {
    public static final long JWT_EXPIRATION = 86400000; // 24 horas en milisegundos
    public static final String JWT_SECRET = "Hackaton2024NoCountryOracleONESentimentAnalysisSecretKeyForJWTGeneration123";

    // En producción estas claves deben estar en variables de entorno
    // public static final String JWT_SECRET = System.getenv("JWT_SECRET");
}