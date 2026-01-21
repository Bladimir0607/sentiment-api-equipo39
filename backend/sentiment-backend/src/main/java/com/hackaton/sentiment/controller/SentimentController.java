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
 * Controlador REST para el análisis de sentimiento de textos.
 *
 * <p>Este controlador expone endpoints que permiten analizar el sentimiento
 * de textos, obtener estadísticas agregadas y consultar el historial de
 * análisis realizados por los usuarios autenticados.</p>
 *
 * <p>La mayoría de las operaciones requieren autenticación previa mediante
 * token JWT.</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 *
 */
@RestController
@RequestMapping("/sentiment")
@RequiredArgsConstructor
@Tag(name = "Sentiment", description = "Análisis de sentimiento")
public class SentimentController {

    private final SentimentService sentimentService;

    /**
     * Analiza el sentimiento de un texto proporcionado por el usuario.
     *
     * <p>El texto es enviado al servicio de Machine Learning, el cual determina
     * el sentimiento asociado (por ejemplo, positivo o negativo) y retorna
     * la predicción junto con información adicional como la etiqueta y la
     * probabilidad.</p>
     *
     * @param request DTO que contiene el texto a analizar
     * @return {@link SentimentResponseDTO} con el resultado del análisis
     */
    @Operation(
            summary = "Analizar sentimiento de un texto",
            description = "Recibe un texto y devuelve la predicción de sentimiento"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Predicción exitosa"),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "503", description = "Servicio de Machine Learning no disponible")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public SentimentResponseDTO analyze(
            @Valid @RequestBody SentimentRequestDTO request) {

        return sentimentService.analyzeSentiment(request);
    }

    /**
     * Obtiene estadísticas globales de los análisis de sentimiento.
     *
     * <p>Este endpoint retorna métricas agregadas de todos los análisis
     * realizados en la plataforma, incluyendo la cantidad de comentarios
     * positivos y negativos.</p>
     *
     * <p>La información proporcionada es útil para obtener una visión general
     * del comportamiento emocional de los usuarios.</p>
     *
     * @return {@link SentimentStatsResponseDTO} con las estadísticas globales
     */
    @Operation(
            summary = "Obtener estadísticas globales de sentimiento",
            description = "Retorna métricas agregadas de todos los análisis de sentimiento registrados en la plataforma, incluyendo totales de comentarios positivos y negativos."
    )
    @GetMapping("/stats")
    public SentimentStatsResponseDTO stats() {
        return sentimentService.getStats();
    }

    /**
     * Obtiene el historial de análisis de sentimiento del usuario actual.
     *
     * <p>Retorna la lista completa de análisis de sentimiento realizados por
     * el usuario autenticado, permitiéndole revisar su historial de
     * interacciones dentro de la plataforma.</p>
     *
     * @return lista de {@link SentimentAnalysis} asociados al usuario actual
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