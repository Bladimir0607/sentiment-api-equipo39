package com.hackaton.sentiment.service.impl;

import com.hackaton.sentiment.client.SentimentMlClient;
import com.hackaton.sentiment.dto.request.SentimentRequestDTO;
import com.hackaton.sentiment.dto.response.SentimentResponseDTO;
import com.hackaton.sentiment.dto.response.SentimentStatsResponseDTO;
import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.entity.User;
import com.hackaton.sentiment.repository.SentimentAnalysisRepository;
import com.hackaton.sentiment.repository.UserRepository;
import com.hackaton.sentiment.service.SentimentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.hackaton.sentiment.util.SentimentLabels.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SentimentServiceImpl implements SentimentService {

    private final SentimentMlClient mlClient;
    private final SentimentAnalysisRepository repository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SentimentResponseDTO analyzeSentiment(SentimentRequestDTO request) {
        //Obtener el usuario autenticado actualmente
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        log.info("🔍 Usuario '{}' analizando texto de {} caracteres",
                username, request.getText().length());

        // Obtener predicción del ML
        SentimentResponseDTO mlResponse = mlClient.predict(request.getText());

        // Crear análisis asociado al usuario
        SentimentAnalysis analysis = SentimentAnalysis.builder()
                .text(request.getText())
                .label(normalizeLabel(mlResponse.getPrediction()))
                .probability(mlResponse.getProbability())
                .user(user)
                .build();

        repository.save(analysis);

        log.info("Análisis guardado para usuario '{}' - Sentimiento: {}",
                username, analysis.getLabel());

        return mlResponse;
    }

    @Override
    public SentimentStatsResponseDTO getStats() {
        long total = repository.count();
        long positive = repository.countByLabel(POSITIVE);
        long negative = repository.countByLabel(NEGATIVE);
        long neutral = repository.countByLabel(NEUTRAL);

        return SentimentStatsResponseDTO.builder()
                .total(total)
                .positive(positive)
                .negative(negative)
                .neutral(neutral)
                .build();
    }

    //Obtener análisis del usuario actual
    public List<SentimentAnalysis> getMyAnalyses() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        return repository.findByUser(user);
    }

    //JOIN FETCH para evitar LazyInitializationException
    public List<SentimentAnalysis> getAllAnalyses() {
        log.info("Obteniendo todos los análisis con usuarios...");
        return repository.findAllWithUser();
    }

    //Obtener estadísticas avanzadas (para ADMIN)
    public Object getAdvancedStats() {
        long totalAnalyses = repository.count();
        long totalUsers = userRepository.count();

        return new java.util.HashMap<String, Object>() {{
            put("totalAnalyses", totalAnalyses);
            put("totalUsers", totalUsers);
            put("avgAnalysesPerUser", totalUsers > 0 ?
                    String.format("%.1f", (double) totalAnalyses / totalUsers) : 0);
            put("analysesBySentiment", new java.util.HashMap<String, Long>() {{
                put("positive", repository.countByLabel(POSITIVE));
                put("negative", repository.countByLabel(NEGATIVE));
                put("neutral", repository.countByLabel(NEUTRAL));
            }});
            put("timestamp", java.time.LocalDateTime.now());
        }};
    }

    // Método de normalización integrado
    private String normalizeLabel(String prediction) {
        if (prediction == null) return NEUTRAL;

        return switch (prediction.toLowerCase()) {
            case "positive", "positivo" -> POSITIVE;
            case "negative", "negativo" -> NEGATIVE;
            case "neutral", "neutro" -> NEUTRAL;
            default -> NEUTRAL;
        };
    }
    @Override
    @Transactional
    public void deleteAnalysesByUser(User user) {
        // Verificar que el usuario existe
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Usuario inválido");
        }

        log.info("Eliminando análisis del usuario: {}", user.getUsername());

        // Buscar y eliminar todos los análisis del usuario
        List<SentimentAnalysis> userAnalyses = repository.findByUser(user);

        if (!userAnalyses.isEmpty()) {
            repository.deleteAll(userAnalyses);
            log.info("Se eliminaron {} análisis del usuario {}",
                    userAnalyses.size(), user.getUsername());
        } else {
            log.info("El usuario {} no tenía análisis para eliminar", user.getUsername());
        }
    }
    @Override
    public List<SentimentAnalysis> getUserAnalyses(Long userId) {
        log.info("Obteniendo análisis del usuario ID: {}", userId);

        // Verificar que el usuario existe
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }

        // Obtener análisis del usuario usando el repositorio
        List<SentimentAnalysis> analyses = repository.findByUserId(userId);

        log.info("Encontrados {} análisis para el usuario ID: {}", analyses.size(), userId);
        return analyses;
    }
}