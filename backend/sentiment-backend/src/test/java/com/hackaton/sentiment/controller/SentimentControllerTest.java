package com.hackaton.sentiment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.sentiment.dto.request.SentimentRequestDTO;
import com.hackaton.sentiment.dto.response.SentimentResponseDTO;
import com.hackaton.sentiment.dto.response.SentimentStatsResponseDTO;
import com.hackaton.sentiment.exception.GlobalExceptionHandler;
import com.hackaton.sentiment.exception.MlServiceException;
import com.hackaton.sentiment.service.SentimentService;
import com.hackaton.sentiment.service.TranslationService;
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

/**
 * Pruebas unitarias para SentimentController.
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@WebMvcTest(SentimentController.class)
@Import(GlobalExceptionHandler.class)
class SentimentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SentimentService sentimentService;

    @MockitoBean
    private TranslationService translationService;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Prueba que analyze retorna 200 con texto válido.
     *
     * @throws Exception sí ocurre un error durante la prueba
     */
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

    /**
     * Prueba que analyze retorna 400 cuando el texto está vacío.
     *
     * @throws Exception sí ocurre un error durante la prueba
     */
    @Test
    void analyze_shouldReturn400_whenTextIsEmpty() throws Exception {
        SentimentRequestDTO request = new SentimentRequestDTO();
        request.setText("");

        mockMvc.perform(post("/sentiment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Prueba que analyze retorna 503 cuando falla el servicio ML.
     *
     * @throws Exception sí ocurre un error durante la prueba
     */
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

    /**
     * Prueba que stats retorna los conteos correctos.
     *
     * @throws Exception sí ocurre un error durante la prueba
     */
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