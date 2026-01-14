package com.hackaton.sentiment.service.impl;

import com.hackaton.sentiment.client.SentimentMlClient;
import com.hackaton.sentiment.dto.request.SentimentRequestDTO;
import com.hackaton.sentiment.dto.response.SentimentResponseDTO;
import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.repository.SentimentAnalysisRepository;
import com.hackaton.sentiment.util.SentimentLabels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SentimentServiceImplTest {

    private SentimentMlClient mlClient;
    private SentimentAnalysisRepository repository;
    private SentimentServiceImpl service;

    @BeforeEach
    void setUp() {
        mlClient = mock(SentimentMlClient.class);
        repository = mock(SentimentAnalysisRepository.class);
        service = new SentimentServiceImpl(mlClient, repository);
    }

    @Test
    void analyzeSentiment_shouldNormalizeLabelAndPersist() {
        // given
        SentimentRequestDTO request = new SentimentRequestDTO("Me encanta este proyecto");

        SentimentResponseDTO mlResponse = SentimentResponseDTO.builder()
                .prediction("positivo")
                .probability(0.95)
                .build();

        when(mlClient.predict(anyString())).thenReturn(mlResponse);

        // when
        SentimentResponseDTO response = service.analyzeSentiment(request);

        // then
        assertThat(response.getPrediction()).isEqualTo("positivo");

        ArgumentCaptor<SentimentAnalysis> captor =
                ArgumentCaptor.forClass(SentimentAnalysis.class);

        verify(repository).save(captor.capture());

        SentimentAnalysis saved = captor.getValue();
        assertThat(saved.getLabel()).isEqualTo(SentimentLabels.POSITIVE);
        assertThat(response.getProbability()).isEqualTo(0.95);
    }

    @Test
    void getStats_shouldReturnCountsByCanonicalLabels() {
        when(repository.count()).thenReturn(10L);
        when(repository.countByLabel(SentimentLabels.POSITIVE)).thenReturn(4L);
        when(repository.countByLabel(SentimentLabels.NEGATIVE)).thenReturn(3L);
        when(repository.countByLabel(SentimentLabels.NEUTRAL)).thenReturn(3L);

        var stats = service.getStats();

        assertThat(stats.getTotal()).isEqualTo(10);
        assertThat(stats.getPositive()).isEqualTo(4);
        assertThat(stats.getNegative()).isEqualTo(3);
        assertThat(stats.getNeutral()).isEqualTo(3);
    }
}
