package com.hackaton.sentiment.controller;

import com.hackaton.sentiment.dto.request.SentimentRequestDTO;
import com.hackaton.sentiment.dto.response.SentimentResponseDTO;
import com.hackaton.sentiment.dto.response.SentimentStatsResponseDTO;
import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.service.SentimentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para el análisis de sentimiento de textos.
 *
 * Proporciona endpoints para analizar textos, obtener estadísticas y consultar
 * el historial de análisis realizados por los usuarios. Requiere autenticación
 * para la mayoría de sus operaciones.
 */
@RestController
@RequestMapping("/sentiment")
@RequiredArgsConstructor
@Tag(name = "Sentiment", description = "Análisis de sentimiento")
public class SentimentController {

    private final SentimentService sentimentService;

    /**
     * Analiza el sentimiento de un texto proporcionado.
     *
     * Envía el texto al servicio de machine learning para determinar su
     * sentimiento (positivo/negativo) y retorna la predicción con detalles
     * como la etiqueta y probabilidad asociada.
     *
     * @param request DTO con el texto a analizar
     * @return {@link SentimentResponseDTO} con los resultados del análisis
     */
    @Operation(
            summary = "Analizar sentimiento de un texto",
            description = "Recibe un texto y devuelve la predicción de sentimiento"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Predicción exitosa"),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "503", description = "Servicio ML no disponible")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public SentimentResponseDTO analyze(
            @Valid @RequestBody SentimentRequestDTO request) {

        return sentimentService.analyzeSentiment(request);
    }

    /**
     * Obtiene estadísticas globales de todos los análisis de sentimiento.
     *
     * Proporciona métricas agregadas de los análisis realizados en la plataforma,
     * incluyendo conteos de comentarios positivos y negativos, útil para análisis
     * general del comportamiento emocional de los usuarios.
     *
     * @return {@link SentimentStatsResponseDTO} con las estadísticas globales
     */
    @Operation(
            summary = "Obtener estadísticas globales de sentimiento",
            description = "Retorna métricas agregadas de todos los análisis de sentimiento registrados en la plataforma, incluyendo totales de comentarios positivos y negativos. " +
                    "Esta información es útil para obtener una visión general del comportamiento emocional de los usuarios."
    )
    @GetMapping("/stats")
    public SentimentStatsResponseDTO stats() {
        return sentimentService.getStats();
    }

    /**
     * Obtiene el historial de análisis realizados por el usuario actual.
     *
     * Retorna la lista completa de análisis de sentimiento que el usuario
     * autenticado ha realizado en la plataforma, permitiendo revisar su
     * historial de interacciones.
     *
     * @return Lista de {@link SentimentAnalysis} del usuario actual
     */
    @Operation(
            summary = "Ver mis análisis",
            description = "Obtiene todos los análisis realizados por el usuario actual"
    )
    @GetMapping("/my-analyses")
    public List<SentimentAnalysis> getMyAnalyses() {
        return sentimentService.getMyAnalyses();
    }
}