package com.hackaton.sentiment.service;

import com.hackaton.sentiment.dto.request.SentimentRequestDTO;
import com.hackaton.sentiment.dto.response.SentimentResponseDTO;
import com.hackaton.sentiment.dto.response.SentimentStatsResponseDTO;
import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.entity.User;
import jakarta.transaction.Transactional;

import java.util.List;

public interface SentimentService {

    @Transactional
    SentimentResponseDTO analyzeSentiment(SentimentRequestDTO request);

    SentimentStatsResponseDTO getStats();

    //USER - ver sus propios análisis
    List<SentimentAnalysis> getMyAnalyses();

    //ADMIN - ver todos los análisis
    List<SentimentAnalysis> getAllAnalyses();

    //ADMIN - estadísticas avanzadas
    Object getAdvancedStats();

    // eliminar user
    @Transactional
    void deleteAnalysesByUser(User user);

    //ADMIN - ver análisis de un usuario específico
    List<SentimentAnalysis> getUserAnalyses(Long userId);
}