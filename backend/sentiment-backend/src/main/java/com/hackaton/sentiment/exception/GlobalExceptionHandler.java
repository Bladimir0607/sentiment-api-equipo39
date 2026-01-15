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

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final TranslationService translationService;

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

    @ExceptionHandler(MlServiceException.class)
    public ResponseEntity<Map<String, String>> handleMlServiceException(
            MlServiceException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", translateIfNeeded(ex.getMessage(), request)));
    }

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

    private String translateIfNeeded(String message, HttpServletRequest request) {
        String acceptLanguage = request.getHeader("Accept-Language");

        if (acceptLanguage == null || acceptLanguage.isBlank()) {
            return message;
        }

        try {
            return translationService.translate(message, acceptLanguage);
        } catch (Exception e) {
            return message;
        }
    }
}