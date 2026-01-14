package com.hackaton.sentiment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleTranslationServiceTest {

    private SimpleTranslationService service;

    @BeforeEach
    void setUp() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage(
                "error.text.required",
                new Locale("es"),
                "El texto no puede estar vacío"
        );
        messageSource.addMessage(
                "error.text.required",
                Locale.ENGLISH,
                "Text cannot be empty"
        );

        service = new SimpleTranslationService(messageSource);
        service.init();
    }

    @Test
    void shouldTranslateUsingSpringMessageSource() {
        String result = service.translate("error.text.required", "es");
        assertThat(result).isEqualTo("El texto no puede estar vacío");
    }

    @Test
    void shouldFallbackToManualTranslation() {
        String result = service.translate("sentiment.label.positive", "pt");
        assertThat(result).isEqualTo("Positivo");
    }

    @Test
    void shouldFallbackToEnglishIfLanguageNotFound() {
        String result = service.translate("error.text.required", "fr");
        assertThat(result).isEqualTo("Text cannot be empty");
    }
}
