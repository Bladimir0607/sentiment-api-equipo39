package com.hackaton.sentiment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.sentiment.dto.request.SentimentRequestDTO;
import com.hackaton.sentiment.dto.response.SentimentResponseDTO;
import com.hackaton.sentiment.dto.response.SentimentStatsResponseDTO;
import com.hackaton.sentiment.exception.GlobalExceptionHandler;
import com.hackaton.sentiment.exception.MlServiceException;
import com.hackaton.sentiment.service.SentimentService;
import com.hackaton.sentiment.service.TranslationService; // 👈 CAMBIO
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SentimentController.class)
@Import(GlobalExceptionHandler.class)
class SentimentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SentimentService sentimentService;

    @MockitoBean
    private TranslationService translationService; // 👈 CAMBIO

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void analyze_shouldReturn200_WhenValidText() throws Exception {
        SentimentRequestDTO request = new SentimentRequestDTO();
        request.setText("El servicio fue excelente");

        SentimentResponseDTO response = new SentimentResponseDTO();
        response.setPrediction("Positivo");
        response.setProbability(0.87);

        when(sentimentService.analyzeSentiment(any())).thenReturn(response);

        mockMvc.perform(post("/sentiment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prediction").value("Positivo"))
                .andExpect(jsonPath("$.probability").value(0.87));
    }

    @Test
    void analyze_shouldReturn400_whenTextIsEmpty() throws Exception {
        SentimentRequestDTO request = new SentimentRequestDTO();
        request.setText("");

        mockMvc.perform(post("/sentiment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void analyze_shouldReturn503_whenMlFails() throws Exception {
        SentimentRequestDTO request = new SentimentRequestDTO();
        request.setText("Texto válido");

        when(sentimentService.analyzeSentiment(any()))
                .thenThrow(new MlServiceException("ML error"));

        mockMvc.perform(post("/sentiment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void stats_shouldReturnCounts() throws Exception {
        SentimentStatsResponseDTO stats = SentimentStatsResponseDTO.builder()
                .total(10L)
                .positive(5L)
                .negative(3L)
                .build();

        when(sentimentService.getStats()).thenReturn(stats);

        mockMvc.perform(get("/sentiment/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.positive").value(5));
    }
}