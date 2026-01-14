package com.hackaton.sentiment.controller;

import com.hackaton.sentiment.service.SimpleTranslationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// Imports estáticos
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(I18nTestController.class)
class I18nControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SimpleTranslationService translationService;

    @Test
    void hello_shouldReturnTranslatedMessage() throws Exception {
        // Simulamos que el servicio siempre devuelve el texto en español
        when(translationService.translate(any(), any()))
                .thenReturn("Análisis de sentimiento completado");

        mockMvc.perform(get("/api/i18n/hello")
                        .header("Accept-Language", "es")) // Probamos el header de idioma
                .andExpect(status().isOk())
                .andExpect(content().string("Análisis de sentimiento completado"));
    }
}