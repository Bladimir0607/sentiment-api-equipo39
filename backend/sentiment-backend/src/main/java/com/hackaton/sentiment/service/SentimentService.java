package com.hackaton.sentiment.service;

import com.hackaton.sentiment.dto.request.SentimentRequestDTO;
import com.hackaton.sentiment.dto.response.SentimentResponseDTO;
import com.hackaton.sentiment.dto.response.SentimentStatsResponseDTO;
import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interfaz de servicio para análisis de sentimiento.
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
public interface SentimentService {

    /**
     * Analiza el sentimiento de un texto.
     *
     * @param request DTO con el texto a analizar
     * @return DTO con la respuesta del análisis
     */
    @Transactional
    SentimentResponseDTO analyzeSentiment(SentimentRequestDTO request);

    /**
     * Obtiene estadísticas globales de análisis.
     *
     * @return DTO con estadísticas calculadas
     */
    SentimentStatsResponseDTO getStats();

    /**
     * Obtiene los análisis del usuario autenticado.
     *
     * @return lista de análisis del usuario
     */
    List<SentimentAnalysis> getMyAnalyses();

    /**
     * Obtiene todos los análisis del sistema (solo admin).
     *
     * @return lista completa de análisis
     */
    List<SentimentAnalysis> getAllAnalyses();

    /**
     * Obtiene estadísticas avanzadas del sistema.
     *
     * @return objeto con estadísticas avanzadas
     */
    Object getAdvancedStats();

    /**
     * Elimina todos los análisis de un usuario.
     *
     * @param user usuario cuyos análisis se eliminan
     */
    @Transactional
    void deleteAnalysesByUser(User user);

    /**
     * Obtiene análisis de un usuario específico (solo admin).
     *
     * @param userId ID del usuario
     * @return lista de análisis del usuario
     */
    List<SentimentAnalysis> getUserAnalyses(Long userId);

    /**
     * Analiza un lote de textos desde un archivo CSV.
     * @param file archivo CSV con columna 'text'
     * @return lista de resultados de análisis
     */
    List<SentimentResponseDTO> analyzeSentimentBatch(MultipartFile file);
}