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

@RestController
@RequestMapping("/sentiment")
@RequiredArgsConstructor
@Tag(name = "Sentiment", description = "Análisis de sentimiento")
public class SentimentController {

    private final SentimentService sentimentService;

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

    @GetMapping("/stats")
    public SentimentStatsResponseDTO stats() {
        return sentimentService.getStats();
    }

    //  USER - ver sus propios análisis
    @Operation(
            summary = "Ver mis análisis",
            description = "Obtiene todos los análisis realizados por el usuario actual"
    )
    @GetMapping("/my-analyses")
    public List<SentimentAnalysis> getMyAnalyses() {
        return sentimentService.getMyAnalyses();
    }
}