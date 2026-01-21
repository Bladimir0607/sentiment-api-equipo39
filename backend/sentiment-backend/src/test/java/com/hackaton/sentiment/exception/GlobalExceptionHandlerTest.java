package com.hackaton.sentiment.exception;

import com.hackaton.sentiment.service.TranslationService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para GlobalExceptionHandler.
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
class GlobalExceptionHandlerTest {

    /**
     * Prueba que handleMlServiceException maneja correctamente la excepción.
     */
    @Test
    void shouldHandleMlServiceException() {
        TranslationService translationService = mock(TranslationService.class);
        GlobalExceptionHandler handler = new GlobalExceptionHandler(translationService);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Accept-Language")).thenReturn("en");
        when(translationService.translate(anyString(), anyString()))
                .thenReturn("Service unavailable");

        var response = handler.handleMlServiceException(
                new MlServiceException("ml.error"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody().get("error"))
                .isEqualTo("Service unavailable");
    }
}