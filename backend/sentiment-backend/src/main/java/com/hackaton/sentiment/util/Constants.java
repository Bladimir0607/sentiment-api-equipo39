package com.hackaton.sentiment.util;

/**
 * Clase de constantes de la aplicación.
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
public final class Constants {

    private Constants() {}

    /**
     * Error del servicio de Machine Learning.
     */
    public static final String ML_SERVICE_ERROR =
            "Error comunicándose con el microservicio de Machine Learning";

    /**
     * Error genérico.
     */
    public static final String GENERIC_ERROR =
            "Ocurrió un error inesperado";

    /**
     * Mensaje de estado saludable.
     */
    public static final String HEALTH_OK = "OK";
}