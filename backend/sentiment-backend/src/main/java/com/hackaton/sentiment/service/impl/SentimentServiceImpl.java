package com.hackaton.sentiment.service.impl;

import com.hackaton.sentiment.client.LibreTranslateClient;
import com.hackaton.sentiment.client.SentimentMlClient;
import com.hackaton.sentiment.dto.request.SentimentRequestDTO;
import com.hackaton.sentiment.dto.response.SentimentResponseDTO;
import com.hackaton.sentiment.dto.response.SentimentStatsResponseDTO;
import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.entity.User;
import com.hackaton.sentiment.repository.SentimentAnalysisRepository;
import com.hackaton.sentiment.service.SentimentService;
import com.hackaton.sentiment.service.TranslationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Collections;

import static com.hackaton.sentiment.util.SentimentLabels.*;

/**
 * Implementación del servicio de análisis de sentimiento con traducción automática.
 *
 * <h2>Arquitectura del Sistema:</h2>
 * <p>Este servicio actúa como <strong>orquestador</strong> entre el frontend,
 * el sistema de traducción (LibreTranslate) y el modelo de Machine Learning.</p>
 *
 * <h2>Flujo Completo de Procesamiento:</h2>
 * <pre>
 * ┌─────────────┐
 * │  FRONTEND   │ Usuario selecciona idioma: EN, ES, PT
 * │  (React)    │ Envía texto: "This is great!"
 * └──────┬──────┘
 *        │ POST /sentiment
 *        │ { "text": "This is great!", "language": "en" }
 *        ▼
 * ┌─────────────────────────────────────────────────────────┐
 * │  BACKEND (Puerto 8080) - Spring Boot                    │
 * │  ┌───────────────────────────────────────────────────┐ │
 * │  │ 1. RECIBIR petición con texto e idioma            │ │
 * │  │    - Texto original: "This is great!"             │ │
 * │  │    - Idioma del usuario: "en"                     │ │
 * │  └───────────────────────────────────────────────────┘ │
 * │  ┌───────────────────────────────────────────────────┐ │
 * │  │ 2. TRADUCIR A ESPAÑOL (si no es español)          │ │
 * │  │    - Detecta que idioma ≠ "es"                    │ │
 * │  │    - Llama a LibreTranslate (Docker)              │ │
 * │  │    - Traduce: "This is great!" → "¡Esto es genial!"│ │
 * │  └───────────────────────────────────────────────────┘ │
 * │  ┌───────────────────────────────────────────────────┐ │
 * │  │ 3. ENVIAR AL MODELO ML (siempre en español)       │ │
 * │  │    POST http://datascience:8000/sentiment         │ │
 * │  │    { "text": "¡Esto es genial!" }                 │ │
 * │  └───────────────────────────────────────────────────┘ │
 * └──────┬──────────────────────────────────────────────────┘
 *        │
 *        ▼
 * ┌─────────────────────────────────────────────────────────┐
 * │  DATA SCIENCE (Puerto 8000) - Python ML                 │
 * │  ┌───────────────────────────────────────────────────┐ │
 * │  │ 4. ANALIZAR SENTIMIENTO (modelo en español)       │ │
 * │  │    - Recibe: "¡Esto es genial!"                   │ │
 * │  │    - TF-IDF + Logistic Regression                 │ │
 * │  │    - Predice: "Positivo" (0.95 probabilidad)      │ │
 * │  └───────────────────────────────────────────────────┘ │
 * │  ┌───────────────────────────────────────────────────┐ │
 * │  │ 5. DEVOLVER RESULTADO (en español)                │ │
 * │  │    { "prediction": "Positivo", "probability": 0.95 }│ │
 * │  └───────────────────────────────────────────────────┘ │
 * └──────┬──────────────────────────────────────────────────┘
 *        │
 *        ▼
 * ┌─────────────────────────────────────────────────────────┐
 * │  BACKEND (Puerto 8080) - Traduce respuesta              │
 * │  ┌───────────────────────────────────────────────────┐ │
 * │  │ 6. TRADUCIR RESULTADO AL IDIOMA ORIGINAL          │ │
 * │  │    - Detecta idioma del usuario: "en"             │ │
 * │  │    - Traduce: "Positivo" → "Positive"             │ │
 * │  │    - Usa TranslationService (i18n)                │ │
 * │  └───────────────────────────────────────────────────┘ │
 * │  ┌───────────────────────────────────────────────────┐ │
 * │  │ 7. DEVOLVER AL FRONTEND (idioma original)         │ │
 * │  │    {                                               │ │
 * │  │      "prediction": "Positive",                     │ │
 * │  │      "probability": 0.95,                          │ │
 * │  │      "originalText": "This is great!",             │ │
 * │  │      "translatedText": "¡Esto es genial!"          │ │
 * │  │    }                                               │ │
 * │  └───────────────────────────────────────────────────┘ │
 * └──────┬──────────────────────────────────────────────────┘
 *        │
 *        ▼
 * ┌─────────────┐
 * │  FRONTEND   │ Muestra resultado en inglés:
 * │  (React)    │ "Positive - 95% confidence"
 * └─────────────┘
 * </pre>
 *
 * <h2>Ventajas de este enfoque:</h2>
 * <ul>
 *   <li><strong>Modelo único:</strong> Solo se entrena en español (simplifica mantenimiento)</li>
 *   <li><strong>Multiidioma:</strong> El usuario puede usar cualquier idioma soportado</li>
 *   <li><strong>Transparencia:</strong> Se devuelven ambos textos (original y traducido)</li>
 *   <li><strong>Escalable:</strong> Fácil agregar más idiomas sin reentrenar el modelo</li>
 * </ul>
 *
 * <h2>Dependencias clave:</h2>
 * <ul>
 *   <li>{@link LibreTranslateClient} - Traducción automática con IA</li>
 *   <li>{@link SentimentMlClient} - Comunicación con modelo de Python</li>
 *   <li>{@link TranslationService} - Sistema i18n para labels/resultados</li>
 *   <li>{@link SentimentAnalysisRepository} - Persistencia de análisis</li>
 * </ul>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 * @see SentimentService
 * @see LibreTranslateClient
 * @see TranslationService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SentimentServiceImpl implements SentimentService {

    private final SentimentMlClient mlClient;
    private final SentimentAnalysisRepository repository;
    private final LibreTranslateClient libreTranslateClient;
    private final TranslationService translationService;

    /**
     * Analiza el sentimiento de un texto con traducción automática bidireccional.
     *
     * <p>Este método implementa el flujo completo de análisis con soporte multiidioma:</p>
     * <ol>
     *   <li>Recibe texto en el idioma del usuario (es, en, pt)</li>
     *   <li>Traduce a español si es necesario (el modelo solo entiende español)</li>
     *   <li>Envía al modelo de Machine Learning para análisis</li>
     *   <li>Guarda el resultado en base de datos</li>
     *   <li>Traduce la respuesta al idioma original del usuario</li>
     *   <li>Devuelve resultado completo con ambos textos</li>
     * </ol>
     *
     * <h3>Ejemplo de uso - Usuario en inglés:</h3>
     * <pre>{@code
     * // Request
     * SentimentRequestDTO request = new SentimentRequestDTO();
     * request.setText("This service is amazing!");
     * request.setLanguage("en");
     *
     * // Procesamiento interno:
     * // 1. originalText = "This service is amazing!"
     * // 2. translatedText = "¡Este servicio es increíble!" (LibreTranslate)
     * // 3. ML predice: "Positivo" con 0.94 de probabilidad
     * // 4. Se traduce "Positivo" → "Positive" (TranslationService)
     *
     * // Response
     * SentimentResponseDTO response = sentimentService.analyzeSentiment(request);
     * // prediction: "Positive"
     * // probability: 0.94
     * // originalText: "This service is amazing!"
     * // translatedText: "¡Este servicio es increíble!"
     * // language: "en"
     * }</pre>
     *
     * <h3>Manejo de idiomas:</h3>
     * <table border="1">
     *   <tr>
     *     <th>Idioma</th>
     *     <th>Traducción entrada</th>
     *     <th>Traducción salida</th>
     *   </tr>
     *   <tr>
     *     <td>es (Español)</td>
     *     <td>No requiere (ya está en español)</td>
     *     <td>No requiere (respuesta directa)</td>
     *   </tr>
     *   <tr>
     *     <td>en (Inglés)</td>
     *     <td>en → es (LibreTranslate)</td>
     *     <td>"Positivo" → "Positive" (i18n)</td>
     *   </tr>
     *   <tr>
     *     <td>pt (Portugués)</td>
     *     <td>pt → es (LibreTranslate)</td>
     *     <td>"Positivo" → "Positivo" (i18n)</td>
     *   </tr>
     * </table>
     *
     * <h3>Persistencia en base de datos:</h3>
     * <p><strong>Importante:</strong> Los análisis se guardan <strong>siempre en español</strong>
     * (el texto traducido), no el texto original del usuario. Esto garantiza consistencia
     * en la base de datos independientemente del idioma de entrada.</p>
     *
     * <h3>Manejo de errores:</h3>
     * <ul>
     *   <li><strong>Fallo de traducción:</strong> Se usa el texto original (el modelo intentará analizarlo)</li>
     *   <li><strong>Fallo del modelo ML:</strong> Se propaga {@link com.hackaton.sentiment.exception.MlServiceException}</li>
     *   <li><strong>Fallo de i18n:</strong> Se devuelve el label en español como fallback</li>
     * </ul>
     *
     * @param request DTO con el texto a analizar y el idioma del usuario
     * @return DTO con la predicción, probabilidad, y ambos textos (original y traducido)
     * @throws com.hackaton.sentiment.exception.MlServiceException si el servicio ML no está disponible
     * @see SentimentRequestDTO
     * @see SentimentResponseDTO
     * @see LibreTranslateClient#translate(String, String, String)
     * @see TranslationService#translate(String, String)
     */
    @Override
    public SentimentResponseDTO analyzeSentiment(SentimentRequestDTO request) {
        String originalText = request.getText();
        String userLanguage = request.getLanguage() != null ? request.getLanguage() : "es";
        String textToAnalyze = originalText;

        log.info("📥 [INICIO] Análisis de sentimiento solicitado");
        log.info("   └─ Texto: '{}'", originalText.length() > 50
                ? originalText.substring(0, 50) + "..."
                : originalText);
        log.info("   └─ Idioma del usuario: {}", userLanguage);

        // ============================================================
        // PASO 1: TRADUCIR A ESPAÑOL (si no es español)
        // ============================================================
        // El modelo de ML solo entiende español, por lo que debemos
        // traducir cualquier texto en otro idioma antes de analizarlo.
        // ============================================================
        if (!"es".equals(userLanguage)) {
            log.info("🌐 [PASO 1] Traduciendo texto de '{}' a 'es' para el modelo ML", userLanguage);

            try {
                // LibreTranslate traduce el texto automáticamente
                // Ejemplo: "This is great!" → "¡Esto es genial!"
                textToAnalyze = libreTranslateClient.translate(originalText, userLanguage, "es");

                log.info("   └─ ✅ Traducción exitosa");
                log.debug("      └─ Texto traducido: '{}'", textToAnalyze.length() > 50
                        ? textToAnalyze.substring(0, 50) + "..."
                        : textToAnalyze);

            } catch (Exception e) {
                log.warn("   └─ ⚠️ Error en traducción, usando texto original: {}", e.getMessage());
                // Si falla la traducción, intentamos analizar el texto original
                textToAnalyze = originalText;
            }
        } else {
            log.info("✅ [PASO 1] Texto ya está en español, no requiere traducción");
        }

        // ============================================================
        // PASO 2: ENVIAR AL MODELO ML (siempre en español)
        // ============================================================
        // El modelo de Data Science espera el texto en español.
        // Se comunica vía HTTP con el servicio Python en puerto 8000.
        // ============================================================
        log.info("🤖 [PASO 2] Enviando al modelo de Machine Learning");
        log.debug("   └─ Texto a analizar: '{}'", textToAnalyze.length() > 50
                ? textToAnalyze.substring(0, 50) + "..."
                : textToAnalyze);

        SentimentResponseDTO mlResponse = mlClient.predict(textToAnalyze);

        log.info("   └─ ✅ Predicción recibida");
        log.info("      └─ Sentimiento: {}", mlResponse.getPrediction());
        log.info("      └─ Confianza: {}%", String.format("%.2f", mlResponse.getProbability() * 100));

        // ============================================================
        // PASO 3: GUARDAR EN BASE DE DATOS (texto en español)
        // ============================================================
        // Importante: Guardamos el texto TRADUCIDO (en español), no el original.
        // Esto mantiene consistencia en la base de datos.
        // ============================================================
        log.info("💾 [PASO 3] Guardando análisis en base de datos");

        SentimentAnalysis analysis = SentimentAnalysis.builder()
                .text(textToAnalyze)  // Texto en español (traducido si era necesario)
                .label(normalizeLabel(mlResponse.getPrediction()))  // POSITIVE o NEGATIVE
                .probability(mlResponse.getProbability())
                .build();

        repository.save(analysis);
        log.debug("   └─ ✅ Análisis guardado con ID: {}", analysis.getId());

        // ============================================================
        // PASO 4: TRADUCIR RESULTADO AL IDIOMA ORIGINAL
        // ============================================================
        // El modelo devuelve "Positivo" o "Negativo" en español.
        // Debemos traducir al idioma que el usuario seleccionó.
        // ============================================================
        String translatedPrediction = mlResponse.getPrediction();

        if (!"es".equals(userLanguage)) {
            log.info("🌐 [PASO 4] Traduciendo resultado de 'es' a '{}'", userLanguage);

            try {
                // Construir la clave de traducción según el label
                // Ejemplo: "sentiment.label.positivo" → buscar traducción en i18n
                String labelKey = "sentiment.label." + mlResponse.getPrediction().toLowerCase();

                // TranslationService usa la estrategia de 4 niveles:
                // 1. Archivos .properties
                // 2. Base de datos
                // 3. LibreTranslate (si no existe)
                // 4. Fallback a inglés
                translatedPrediction = translationService.translate(labelKey, userLanguage);

                log.info("   └─ ✅ Label traducido: '{}' → '{}'",
                        mlResponse.getPrediction(), translatedPrediction);

            } catch (Exception e) {
                log.warn("   └─ ⚠️ Error traduciendo label, usando español: {}", e.getMessage());
                translatedPrediction = mlResponse.getPrediction();
            }
        } else {
            log.info("✅ [PASO 4] Resultado ya está en español, no requiere traducción");
        }

        // ============================================================
        // PASO 5: CONSTRUIR Y DEVOLVER RESPUESTA COMPLETA
        // ============================================================
        log.info("📤 [PASO 5] Construyendo respuesta para el frontend");

        SentimentResponseDTO response = SentimentResponseDTO.builder()
                .prediction(translatedPrediction)      // Label en idioma del usuario
                .probability(mlResponse.getProbability())
                .originalText(originalText)            // Texto que envió el usuario
                .translatedText(textToAnalyze)         // Texto que analizó el modelo (en español)
                .language(userLanguage)                // Idioma del usuario
                .build();

        log.info("✅ [FIN] Análisis completado exitosamente");
        log.info("   └─ Predicción final: {} ({}%)",
                response.getPrediction(),
                String.format("%.2f", response.getProbability() * 100));

        return response;
    }

    /**
     * Obtiene estadísticas agregadas de todos los análisis realizados.
     *
     * <p>Calcula el total de análisis y los separa por clasificación
     * (positivo/negativo). Útil para dashboards y reportes.</p>
     *
     * <h3>Ejemplo de respuesta:</h3>
     * <pre>{@code
     * {
     *   "total": 150,
     *   "positive": 95,
     *   "negative": 55,
     *   "positivePercentage": 63.33,
     *   "negativePercentage": 36.67
     * }
     * }</pre>
     *
     * <h3>Casos de uso:</h3>
     * <ul>
     *   <li>Dashboard de satisfacción del cliente</li>
     *   <li>Reportes de análisis de sentimiento</li>
     *   <li>KPIs de atención al cliente</li>
     *   <li>Monitoreo de campañas de marketing</li>
     * </ul>
     *
     * @return DTO con estadísticas agregadas de análisis
     * @see SentimentStatsResponseDTO
     */
    @Override
    public SentimentStatsResponseDTO getStats() {
        log.info("📊 Calculando estadísticas de análisis de sentimiento");

        long total = repository.count();
        long positive = repository.countByLabel(POSITIVE);
        long negative = repository.countByLabel(NEGATIVE);

        log.debug("   └─ Total: {}, Positivos: {}, Negativos: {}", total, positive, negative);

        return SentimentStatsResponseDTO.builder()
                .total(total)
                .positive(positive)
                .negative(negative)
                .build();
    }

    /**
     * Normaliza el label de predicción del modelo a formato canónico.
     *
     * <p>El modelo de ML puede devolver las predicciones en diferentes formatos
     * (con mayúsculas, minúsculas, acentos, etc.). Este método las normaliza
     * a los valores canónicos {@link com.hackaton.sentiment.util.SentimentLabels}.</p>
     *
     * <h3>Mapeo de normalizaciones:</h3>
     * <table border="1">
     *   <tr>
     *     <th>Entrada del modelo</th>
     *     <th>Salida normalizada</th>
     *   </tr>
     *   <tr>
     *     <td>"Positivo", "positivo", "POSITIVO", "positive", "Positive"</td>
     *     <td>{@code SentimentLabels.POSITIVE}</td>
     *   </tr>
     *   <tr>
     *     <td>"Negativo", "negativo", "NEGATIVO", "negative", "Negative"</td>
     *     <td>{@code SentimentLabels.NEGATIVE}</td>
     *   </tr>
     *   <tr>
     *     <td>null, "", cualquier otro valor</td>
     *     <td>{@code SentimentLabels.NEGATIVE} (fallback seguro)</td>
     *   </tr>
     * </table>
     *
     * <h3>Justificación del fallback a NEGATIVE:</h3>
     * <p>En caso de recibir un valor inesperado o null, se clasifica como
     * NEGATIVE por precaución. Esto es preferible en contextos de atención
     * al cliente donde es mejor revisar un caso dudoso que ignorar
     * un posible problema.</p>
     *
     * @param prediction Label de predicción del modelo ML (puede ser en español o inglés)
     * @return Label normalizado ({@code POSITIVE} o {@code NEGATIVE})
     * @see com.hackaton.sentiment.util.SentimentLabels
     */
    private String normalizeLabel(String prediction) {
        if (prediction == null || prediction.isBlank()) {
            log.warn("⚠️ Predicción null o vacía, usando NEGATIVE como fallback");
            return NEGATIVE;
        }

        String normalized = switch (prediction.toLowerCase().trim()) {
            case "positive", "positivo" -> POSITIVE;
            case "negative", "negativo" -> NEGATIVE;
            default -> {
                log.warn("⚠️ Label desconocido: '{}', usando NEGATIVE como fallback", prediction);
                yield NEGATIVE;
            }
        };

        log.debug("🔄 Label normalizado: '{}' → '{}'", prediction, normalized);
        return normalized;
    }

    @Override
    public List<SentimentAnalysis> getMyAnalyses() {
        log.info("📄 Obteniendo análisis del usuario autenticado");
        // TODO: filtrar por usuario autenticado cuando se integre SecurityContext
        return repository.findAll();
    }

    @Override
    public List<SentimentAnalysis> getAllAnalyses() {
        log.info("📂 ADMIN - obteniendo todos los análisis del sistema");
        return repository.findAll();
    }

    @Override
    public Object getAdvancedStats() {
        log.info("📊 ADMIN - obteniendo estadísticas avanzadas");
        // TODO: implementar métricas avanzadas (promedios, tendencias, etc.)
        return Collections.emptyMap();
    }

    @Override
    @Transactional
    public void deleteAnalysesByUser(User user) {
        log.info("🗑️ Eliminando análisis del usuario con ID: {}", user.getId());
        repository.deleteByUser(user);
    }

    @Override
    public List<SentimentAnalysis> getUserAnalyses(Long userId) {
        log.info("📄 ADMIN - obteniendo análisis del usuario con ID: {}", userId);
        return repository.findByUserId(userId);
    }

}