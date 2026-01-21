package com.hackaton.sentiment.service.impl;

import com.hackaton.sentiment.client.LibreTranslateClient;
import com.hackaton.sentiment.client.SentimentMlClient;
import com.hackaton.sentiment.dto.request.SentimentRequestDTO;
import com.hackaton.sentiment.dto.response.SentimentResponseDTO;
import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.repository.SentimentAnalysisRepository;
import com.hackaton.sentiment.service.TranslationService;
import com.hackaton.sentiment.util.SentimentLabels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para SentimentServiceImpl.
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
class SentimentServiceImplTest {

    private SentimentMlClient mlClient;
    private SentimentAnalysisRepository repository;
    private LibreTranslateClient libreTranslateClient;
    private TranslationService translationService;
    private SentimentServiceImpl service;

    @BeforeEach
    void setUp() {
        mlClient = mock(SentimentMlClient.class);
        repository = mock(SentimentAnalysisRepository.class);
        libreTranslateClient = mock(LibreTranslateClient.class);
        translationService = mock(TranslationService.class);

        service = new SentimentServiceImpl(
                mlClient,
                repository,
                libreTranslateClient,
                translationService
        );
    }

    /**
     * Prueba que analyzeSentiment normaliza etiqueta y persiste cuando el texto está en español.
     */
    @Test
    void analyzeSentiment_shouldNormalizeLabelAndPersist_whenTextInSpanish() {
        SentimentRequestDTO request = new SentimentRequestDTO();
        request.setText("Me encanta este proyecto");
        request.setLanguage("es");

        SentimentResponseDTO mlResponse = SentimentResponseDTO.builder()
                .prediction("Positivo")
                .probability(0.95)
                .build();

        when(mlClient.predict(anyString())).thenReturn(mlResponse);

        SentimentResponseDTO response = service.analyzeSentiment(request);

        assertThat(response.getPrediction()).isEqualTo("Positivo");
        assertThat(response.getProbability()).isEqualTo(0.95);
        assertThat(response.getOriginalText()).isEqualTo("Me encanta este proyecto");
        assertThat(response.getTranslatedText()).isEqualTo("Me encanta este proyecto");
        assertThat(response.getLanguage()).isEqualTo("es");

        verify(libreTranslateClient, never()).translate(anyString(), anyString(), anyString());

        ArgumentCaptor<SentimentAnalysis> captor =
                ArgumentCaptor.forClass(SentimentAnalysis.class);
        verify(repository).save(captor.capture());

        SentimentAnalysis saved = captor.getValue();
        assertThat(saved.getLabel()).isEqualTo(SentimentLabels.POSITIVE);
        assertThat(saved.getText()).isEqualTo("Me encanta este proyecto");
        assertThat(saved.getProbability()).isEqualTo(0.95);
    }

    /**
     * Prueba que analyzeSentiment traduce a español cuando el texto está en inglés.
     */
    @Test
    void analyzeSentiment_shouldTranslateToSpanish_whenTextInEnglish() {
        SentimentRequestDTO request = new SentimentRequestDTO();
        request.setText("This is great!");
        request.setLanguage("en");

        when(libreTranslateClient.translate("This is great!", "en", "es"))
                .thenReturn("¡Esto es genial!");

        SentimentResponseDTO mlResponse = SentimentResponseDTO.builder()
                .prediction("Positivo")
                .probability(0.92)
                .build();
        when(mlClient.predict("¡Esto es genial!")).thenReturn(mlResponse);

        when(translationService.translate("sentiment.label.positivo", "en"))
                .thenReturn("Positive");

        SentimentResponseDTO response = service.analyzeSentiment(request);

        assertThat(response.getPrediction()).isEqualTo("Positive");
        assertThat(response.getProbability()).isEqualTo(0.92);
        assertThat(response.getOriginalText()).isEqualTo("This is great!");
        assertThat(response.getTranslatedText()).isEqualTo("¡Esto es genial!");
        assertThat(response.getLanguage()).isEqualTo("en");

        verify(libreTranslateClient).translate("This is great!", "en", "es");
        verify(translationService).translate("sentiment.label.positivo", "en");

        ArgumentCaptor<SentimentAnalysis> captor =
                ArgumentCaptor.forClass(SentimentAnalysis.class);
        verify(repository).save(captor.capture());

        SentimentAnalysis saved = captor.getValue();
        assertThat(saved.getText()).isEqualTo("¡Esto es genial!");
        assertThat(saved.getLabel()).isEqualTo(SentimentLabels.POSITIVE);
    }

    /**
     * Prueba que analyzeSentiment traduce a español cuando el texto está en portugués.
     */
    @Test
    void analyzeSentiment_shouldTranslateToSpanish_whenTextInPortuguese() {
        SentimentRequestDTO request = new SentimentRequestDTO();
        request.setText("Isso é ótimo!");
        request.setLanguage("pt");

        when(libreTranslateClient.translate("Isso é ótimo!", "pt", "es"))
                .thenReturn("¡Esto es genial!");

        SentimentResponseDTO mlResponse = SentimentResponseDTO.builder()
                .prediction("Positivo")
                .probability(0.88)
                .build();
        when(mlClient.predict("¡Esto es genial!")).thenReturn(mlResponse);

        when(translationService.translate("sentiment.label.positivo", "pt"))
                .thenReturn("Positivo");

        SentimentResponseDTO response = service.analyzeSentiment(request);

        assertThat(response.getPrediction()).isEqualTo("Positivo");
        assertThat(response.getOriginalText()).isEqualTo("Isso é ótimo!");
        assertThat(response.getTranslatedText()).isEqualTo("¡Esto es genial!");
        assertThat(response.getLanguage()).isEqualTo("pt");

        verify(libreTranslateClient).translate("Isso é ótimo!", "pt", "es");
        verify(translationService).translate("sentiment.label.positivo", "pt");
    }

    /**
     * Prueba que analyzeSentiment maneja sentimiento negativo.
     */
    @Test
    void analyzeSentiment_shouldHandleNegativeSentiment() {
        SentimentRequestDTO request = new SentimentRequestDTO();
        request.setText("Este servicio es terrible");
        request.setLanguage("es");

        SentimentResponseDTO mlResponse = SentimentResponseDTO.builder()
                .prediction("Negativo")
                .probability(0.89)
                .build();

        when(mlClient.predict(anyString())).thenReturn(mlResponse);

        SentimentResponseDTO response = service.analyzeSentiment(request);

        ArgumentCaptor<SentimentAnalysis> captor =
                ArgumentCaptor.forClass(SentimentAnalysis.class);
        verify(repository).save(captor.capture());

        SentimentAnalysis saved = captor.getValue();
        assertThat(saved.getLabel()).isEqualTo(SentimentLabels.NEGATIVE);
    }

    /**
     * Prueba que getStats retorna conteos por etiquetas canónicas.
     */
    @Test
    void getStats_shouldReturnCountsByCanonicalLabels() {
        when(repository.count()).thenReturn(10L);
        when(repository.countByLabel(SentimentLabels.POSITIVE)).thenReturn(6L);
        when(repository.countByLabel(SentimentLabels.NEGATIVE)).thenReturn(4L);

        var stats = service.getStats();

        assertThat(stats.getTotal()).isEqualTo(10);
        assertThat(stats.getPositive()).isEqualTo(6);
        assertThat(stats.getNegative()).isEqualTo(4);
        assertThat(stats.getPositivePercentage()).isEqualTo(60.0);
        assertThat(stats.getNegativePercentage()).isEqualTo(40.0);
    }
}