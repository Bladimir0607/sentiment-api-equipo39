package com.hackaton.sentiment.controller;

import com.hackaton.sentiment.client.LibreTranslateClient;
import com.hackaton.sentiment.service.TranslationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(I18nController.class)
class I18nControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TranslationService translationService;

    @MockitoBean
    private LibreTranslateClient libreTranslateClient;

    @Test
    void getTranslation_shouldReturnTranslatedMessage() throws Exception {
        when(translationService.translate(any(), any()))
                .thenReturn("Text cannot be empty");

        mockMvc.perform(get("/api/i18n/translate")
                        .param("key", "error.text.required")
                        .param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.translation").value("Text cannot be empty"))
                .andExpect(jsonPath("$.success").value("true"));
    }

    @Test
    void getAllTranslations_shouldReturnMap() throws Exception {
        when(translationService.getAllTranslationsForLanguage("es"))
                .thenReturn(Map.of("error.text.required", "El texto no puede estar vacío"));

        mockMvc.perform(get("/api/i18n/translations/es"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['error.text.required']").value("El texto no puede estar vacío"));
    }

    @Test
    void sayHello_shouldReturnTranslatedMessage() throws Exception {
        when(translationService.translate(any(), any()))
                .thenReturn("Análisis de sentimiento completado");

        mockMvc.perform(get("/api/i18n/hello")
                        .header("Accept-Language", "es"))
                .andExpect(status().isOk())
                .andExpect(content().string("Análisis de sentimiento completado"));
    }
}