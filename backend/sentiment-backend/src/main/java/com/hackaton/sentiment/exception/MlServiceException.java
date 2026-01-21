package com.hackaton.sentiment.exception;

/**
 * Excepción personalizada para errores relacionados con servicios de
 * Machine Learning o servicios externos de análisis.
 *
 * <p>Esta excepción se utiliza para encapsular fallos como:
 * <ul>
 *     <li>Servicios de ML no disponibles</li>
 *     <li>Timeouts en llamadas externas</li>
 *     <li>Errores de comunicación con APIs de análisis</li>
 * </ul>
 *
 * <p>Permite diferenciar errores de infraestructura externa
 * de errores internos de la aplicación.</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
public class MlServiceException extends RuntimeException {

    /**
     * Crea una nueva excepción con un mensaje descriptivo.
     *
     * @param message mensaje que describe la causa del error
     */
    public MlServiceException(String message) {
        super(message);
    }

    /**
     * Crea una nueva excepción con un mensaje descriptivo y la causa original.
     *
     * <p>Útil para encapsular excepciones de bajo nivel
     * manteniendo el stack tracé original.</p>
     *
     * @param message mensaje que describe la causa del error
     * @param cause excepción original que provocó el fallo
     */
    public MlServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}