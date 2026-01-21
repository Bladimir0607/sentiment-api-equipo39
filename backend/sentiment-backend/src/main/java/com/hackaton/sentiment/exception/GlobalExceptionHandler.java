package com.hackaton.sentiment.exception;

import com.hackaton.sentiment.service.TranslationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Manejador global de excepciones para la aplicación.
 *
 * <p>Esta clase centraliza el manejo de errores lanzados por los controladores REST,
 * garantizando respuestas consistentes, controladas y opcionalmente traducidas
 * según el idioma solicitado por el cliente.</p>
 *
 * <p>Utiliza {@link RestControllerAdvice} para interceptar excepciones
 * de forma transversal en toda la aplicación.</p>
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /**
     * Servicio encargado de traducir mensajes de error
     * según el idioma especificado en la cabecera {@code Accept-Language}.
     */
    private final TranslationService translationService;

    /**
     * Maneja errores de validación de argumentos en peticiones REST.
     *
     * <p>Se activa cuando falla la validación de un DTO anotado con
     * {@code @Valid} o restricciones de Jakarta Validation.</p>
     *
     * @param ex excepción lanzada por Spring al fallar la validación
     * @param request solicitud HTTP entrante
     * @return respuesta HTTP 400 con un mensaje de error traducido sí aplica
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(e -> e.getDefaultMessage())
                .orElse("error.validation");

        return ResponseEntity.badRequest()
                .body(Map.of("error", translateIfNeeded(message, request)));
    }

    /**
     * Maneja excepciones relacionadas con fallos en el servicio de Machine Learning.
     *
     * <p>Generalmente, se utiliza cuando un servicio externo
     * (por ejemplo, análisis de sentimiento) no está disponible.</p>
     *
     * @param ex excepción personalizada del servicio ML
     * @param request solicitud HTTP entrante
     * @return respuesta HTTP 503 con mensaje traducido sí aplica
     */
    @ExceptionHandler(MlServiceException.class)
    public ResponseEntity<Map<String, String>> handleMlServiceException(
            MlServiceException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", translateIfNeeded(ex.getMessage(), request)));
    }

    /**
     * Maneja cualquier excepción no controlada explícitamente.
     *
     * <p>Este método actúa como un fallback global para evitar exponer
     * información sensible del sistema al cliente.</p>
     *
     * @param ex excepción genérica capturada
     * @param request solicitud HTTP entrante
     * @return respuesta HTTP 500 con mensaje genérico de error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error",
                        translateIfNeeded("error.internal.server", request)
                ));
    }

    /**
     * Traduce un mensaje de error si el cliente especifica un idioma.
     *
     * <p>La traducción se basa en el valor de la cabecera {@code Accept-Language}.
     * Si la traducción falla o no existe, se devuelve el mensaje original.</p>
     *
     * @param message mensaje de error original o clave de traducción
     * @param request solicitud HTTP entrante
     * @return mensaje traducido o mensaje original si no se pudo traducir
     */
    private String translateIfNeeded(String message, HttpServletRequest request) {
        String acceptLanguage = request.getHeader("Accept-Language");

        if (acceptLanguage == null || acceptLanguage.isBlank()) {
            return message;
        }

        try {
            return translationService.translate(message, acceptLanguage);
        } catch (Exception e) {
            // En caso de error en traducción, se retorna el mensaje original
            return message;
        }
    }
}