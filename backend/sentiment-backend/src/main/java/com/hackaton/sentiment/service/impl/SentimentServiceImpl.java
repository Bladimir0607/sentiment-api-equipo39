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

/**
 * Implementación del servicio para análisis de sentimiento.
 *
 * Proporciona la lógica de negocio para procesar análisis de texto, gestionar
 * estadísticas y realizar operaciones relacionadas con el historial de análisis
 * de los usuarios.
 *
 * Se integra con el cliente de machine learning para obtener predicciones
 * y gestiona la persistencia de análisis en la base de datos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SentimentServiceImpl implements SentimentService {

    private final SentimentMlClient mlClient;
    private final SentimentAnalysisRepository repository;
    private final UserRepository userRepository;

    /**
     * Analiza el sentimiento de un texto y guarda el resultado asociado al usuario actual.
     *
     * Obtiene la predicción del servicio ML, normaliza la etiqueta a un formato binario
     * (POSITIVE/NEGATIVE) y guarda el análisis en la base de datos asociado al usuario
     * autenticado.
     *
     * @param request DTO con el texto a analizar
     * @return {@link SentimentResponseDTO} con la respuesta del servicio ML
     * @throws RuntimeException si el usuario autenticado no se encuentra en la base de datos
     */
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

    /**
     * Obtiene estadísticas globales de los análisis de sentimiento.
     *
     * Calcula el total de análisis, así como los conteos de análisis positivos
     * y negativos registrados en el sistema.
     *
     * @return {@link SentimentStatsResponseDTO} con las estadísticas calculadas
     */
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

    /**
     * Obtiene todos los análisis realizados por el usuario actualmente autenticado.
     *
     * @return Lista de {@link SentimentAnalysis} del usuario autenticado
     * @throws RuntimeException si el usuario autenticado no se encuentra en la base de datos
     */
    public List<SentimentAnalysis> getMyAnalyses() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        return repository.findByUser(user);
    }

    /**
     * Obtiene todos los análisis del sistema incluyendo la información de usuarios.
     *
     * <p>Utiliza JOIN FETCH para evitar el problema N+1 y optimizar la carga de
     * la relación con los usuarios.</p>
     *
     * JOIN FETCH para evitar LazyInitializationException
     *
     * @return Lista de todos los {@link SentimentAnalysis} ordenados por fecha descendente
     */

    public List<SentimentAnalysis> getAllAnalyses() {
        log.info("Obteniendo todos los análisis con usuarios...");
        return repository.findAllWithUser();
    }

    /**
     * Obtiene estadísticas avanzadas del sistema para uso administrativo (para ADMIN).
     *
     * Incluye métricas como total de análisis, total de usuarios, promedio de
     * análisis por usuario y distribución de sentimientos.
     *
     * @return Mapa con las estadísticas avanzadas calculadas
     */
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

    /**
     * Normaliza la etiqueta de predicción a un formato binario (POSITIVE/NEGATIVE).
     *
     * Convierte cualquier variación de etiquetas de sentimiento a solo dos
     * categorías: POSITIVE o NEGATIVE, eliminando categorías intermedias como NEUTRAL.
     *
     * @param prediction Etiqueta de predicción original del servicio ML
     * @return Etiqueta normalizada (POSITIVE o NEGATIVE)
     */
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

    /**
     * Elimina todos los análisis asociados a un usuario específico.
     *
     * @param user Usuario cuyos análisis se desean eliminar
     * @throws IllegalArgumentException si el usuario es nulo o no tiene ID válido
     */
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

    /**
     * Obtiene todos los análisis de sentimiento de un usuario específico por su ID.
     *
     * @param userId ID del usuario cuyos análisis se desean consultar
     * @return Lista de análisis del usuario especificado
     * @throws RuntimeException si el usuario no existe
     */
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

    /**
     * Método adicional de utilidad para migrar análisis con etiqueta NEUTRAL a NEGATIVE.
     *
     * <p>Puede utilizarse para actualizar datos existentes si se cambia de un
     * sistema de tres categorías (POSITIVE/NEUTRAL/NEGATIVE) a uno binario
     * (POSITIVE/NEGATIVE).</p>
     *
     * Actualmente comentado, se puede habilitar según necesidades específicas.
     */
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