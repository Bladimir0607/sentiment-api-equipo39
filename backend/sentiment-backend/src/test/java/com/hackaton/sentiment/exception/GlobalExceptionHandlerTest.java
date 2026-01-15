package com.hackaton.sentiment.exception;

import com.hackaton.sentiment.service.TranslationService; // 👈 CAMBIO
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @Test
    void shouldHandleMlServiceException() {
        TranslationService translationService = mock(TranslationService.class); // 👈 CAMBIO
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