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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // Obtener el usuario autenticado actualmente
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        log.info("🔍 Usuario '{}' analizando texto de {} caracteres",
                username, request.getText().length());

        // Obtener predicción del ML (ahora con palabras clave desde /sentiment-explain)
        SentimentResponseDTO mlResponse = mlClient.predict(request.getText());

        // Normalizar etiqueta (solo Positivo/Negativo)
        String normalizedLabel = normalizeLabelBinary(mlResponse.getPrediction());

        // Crear análisis asociado al usuario
        SentimentAnalysis analysis = SentimentAnalysis.builder()
                .text(request.getText())
                .label(normalizedLabel)
                .probability(mlResponse.getProbability())
                .user(user)
                .build();

        repository.save(analysis);

        log.info("✅ Análisis guardado para usuario '{}' - Sentimiento: {} (Probabilidad: {}, Palabras clave: {})",
                username, analysis.getLabel(), mlResponse.getProbability(),
                mlResponse.getKeyWords() != null ? mlResponse.getKeyWords().size() : 0);

        return mlResponse;
    }

    @Override
    public SentimentStatsResponseDTO getStats() {
        long total = repository.count();
        long positive = repository.countByLabel(POSITIVE);
        long negative = repository.countByLabel(NEGATIVE);

        return SentimentStatsResponseDTO.builder()
                .total(total)
                .positive(positive)
                .negative(negative)
                .build();
    }

    // Obtener análisis del usuario actual
    public List<SentimentAnalysis> getMyAnalyses() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        return repository.findByUser(user);
    }

    // JOIN FETCH para evitar LazyInitializationException
    public List<SentimentAnalysis> getAllAnalyses() {
        log.info("Obteniendo todos los análisis con usuarios...");
        return repository.findAllWithUser();
    }

    // Obtener estadísticas avanzadas (para ADMIN)
    public Object getAdvancedStats() {
        long totalAnalyses = repository.count();
        long totalUsers = userRepository.count();
        long positive = repository.countByLabel(POSITIVE);
        long negative = repository.countByLabel(NEGATIVE);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAnalyses", totalAnalyses);
        stats.put("totalUsers", totalUsers);
        stats.put("avgAnalysesPerUser", totalUsers > 0 ?
                String.format("%.1f", (double) totalAnalyses / totalUsers) : 0);

        // Solo Positivo/Negativo
        Map<String, Long> sentimentMap = new HashMap<>();
        sentimentMap.put("positive", positive);
        sentimentMap.put("negative", negative);

        stats.put("analysesBySentiment", sentimentMap);
        stats.put("timestamp", LocalDateTime.now());

        return stats;
    }

    // Método de normalización BINARIO (sin neutro)
    private String normalizeLabelBinary(String prediction) {
        if (prediction == null) {
            return NEGATIVE; // Por defecto, si es nulo
        }

        String lowerPrediction = prediction.toLowerCase();

        // Solo Positivo/Negativo
        if (lowerPrediction.contains("positiv") ||
                "positivo".equals(lowerPrediction) ||
                "positive".equals(lowerPrediction)) {
            return POSITIVE;
        }

        // Cualquier otra cosa (incluyendo "negativo", "negative", etc.) es NEGATIVE
        return NEGATIVE;
    }

    @Override
    @Transactional
    public void deleteAnalysesByUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Usuario inválido");
        }

        log.info("Eliminando análisis del usuario: {}", user.getUsername());

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

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }

        List<SentimentAnalysis> analyses = repository.findByUserId(userId);
        log.info("Encontrados {} análisis para el usuario ID: {}", analyses.size(), userId);
        return analyses;
    }

    // Método adicional: Migrar análisis existentes con NEUTRAL a NEGATIVE (si tienes datos antiguos)
    @Transactional
    public void migrateNeutralAnalyses() {
        try {
            // Si aún existe la constante NEUTRAL en el código o en la base de datos
            // List<SentimentAnalysis> neutralAnalyses = repository.findByLabel("NEUTRAL");
            // if (!neutralAnalyses.isEmpty()) {
            //     log.info("Migrando {} análisis NEUTRAL a NEGATIVO", neutralAnalyses.size());
            //     neutralAnalyses.forEach(analysis -> analysis.setLabel(NEGATIVE));
            //     repository.saveAll(neutralAnalyses);
            // }
        } catch (Exception e) {
            log.warn("No se pudieron migrar análisis neutrales: {}", e.getMessage());
        }
    }
}